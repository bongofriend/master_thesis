import argparse
from os import path, mkdir, listdir
import logging
import tempfile
import shutil
import zipfile
import sys
from xml.dom.minidom import Element, parse
from typing import List
from models import RoleEntry, RoleDescriptor
from pathlib import Path
import os

class Config:
    source_files_archive_dir: str
    dataset_dir: str
    __tmpDir: tempfile.TemporaryDirectory
    source_dir: str

    def __init__(self, source_files_archive_dir: str, dataset_dir: str):
        current_location = path.dirname(path.realpath(sys.argv[0]))
        self.source_files_archive_dir = path.realpath(path.join(current_location, source_files_archive_dir))
        self.dataset_dir = path.realpath(path.join(current_location, dataset_dir))
        
        self.__tmpDir = tempfile.TemporaryDirectory()
        self.source_dir = path.join(self.get_tmp_dir(), 'sources')
    
    def get_tmp_dir(self) -> str:
        return self.__tmpDir.name
    
    def clean_up(self):
        self.__tmpDir.cleanup()

    @staticmethod
    def default() -> "Config":
        return Config('./source_files.zip', './dataset')


def parse_args() -> Config:
    parser = argparse.ArgumentParser(
        prog='Unzip and extract files for dataset'
    )
    parser.add_argument('--sourceFilesArchive', type=str, help='Location of archive with source files', dest='archive_dir', required=True)
    parser.add_argument('--datasetDir', type=str, help='Directory to where dataset files are to be located', dest='dataset_dir', required=True)
    #parser.add_argument('--sourceDir', type=str, help='Directory where unpr')
    args = parser.parse_args()
    return Config(args.archive_dir, args.dataset_dir)

def unzip_source_files(config: Config):
    logging.info('Extracting source files...')
    with zipfile.ZipFile(config.source_files_archive_dir, mode='r') as zip:
        zip.extractall(config.get_tmp_dir())

    for d in listdir(config.get_tmp_dir()):
        p = path.join(config.get_tmp_dir(), d)
        if p.endswith('psmart.xml'):
            if not path.exists(config.dataset_dir):
                mkdir(config.dataset_dir)
            shutil.copyfile(p, path.join(config.dataset_dir, 'psmart.xml'))
            continue
        if not zipfile.is_zipfile(p):
            continue
        with zipfile.ZipFile(p, mode='r') as zip:
            logging.info(f'Extracting {p}...')
            zip.extractall(config.source_dir)

def parse_micro_arch(micro_arch_node: Element, pattern_path: str, project_name: str):
    arch_number = micro_arch_node.getAttribute('number')
    micro_arch_name = f'micro_arch_{arch_number}'
    micro_arch_path = path.join(pattern_path, micro_arch_name)
    if path.exists(micro_arch_path):
        logging.info(f'Duplicate micro architecture for {micro_arch_name}')
        return
    else:
        mkdir(micro_arch_path)
    role_descriptor = RoleDescriptor.from_xml(micro_arch_node)
    if len(role_descriptor.roleEntries) == 0:
        return
    role_descriptor.to_csv(path.join(micro_arch_path, 'roles.csv'))
    with open(path.join(micro_arch_path, 'project.txt'), mode='w') as f:
        f.write(project_name)


def parse_pattern_node(config: Config, project_name: str, pattern_node: Element):
    pattern_name = pattern_node.getAttribute('name')
    logging.info(f'Extracting {pattern_name} from {project_name}')
    pattern_path = path.join(config.dataset_dir, pattern_name.lower().replace(' ', '_'))
    if not path.exists(pattern_path):
        mkdir(pattern_path)
    for micro_arch_node in pattern_node.getElementsByTagName('microArchitecture'):
        parse_micro_arch(micro_arch_node, pattern_path, project_name)


def extract_metadata_from_source(config: Config):
    xml_config_path = path.join(config.dataset_dir, 'psmart.xml')
    if not path.exists(xml_config_path):
        raise FileNotFoundError()
    with open(xml_config_path, mode='r') as xml_content:
        document = parse(xml_content)
        for programNode in document.getElementsByTagName('program'):
            project_name = programNode.getElementsByTagName('name')[0].firstChild.nodeValue
            logging.info(f'Extracting design patterns and roles for {project_name}')
            for pattern_node in programNode.getElementsByTagName('designPattern'):
                parse_pattern_node(config, project_name, pattern_node)

def resolve_source_file_path(config: Config, role: RoleEntry, project_name) -> (str, str):
    base_path = path.join(config.get_tmp_dir(), 'sources', project_name)
    parsed_path_segments = []
    for seg in role.entity.split('.'):
        parsed_path_segments.append(seg)
        if len(seg) == 0 or seg[0].islower():
            continue
        possible_paths = [
            f"{path.join(base_path, 'src', os.sep.join(parsed_path_segments))}.java",
            f"{path.join(base_path, os.sep.join(parsed_path_segments))}.java"
        ]
        for p in possible_paths:
            if path.exists(p) and path.isfile(p):
                return (p, seg)
    return None

def move_source_files(config: Config):
    for dp in filter(lambda s: path.isdir(path.join(config.dataset_dir, s)), listdir(path.join(config.dataset_dir))):
        for micro_arch in listdir(path.join(config.dataset_dir, dp)):
            micro_arch_dir = path.join(config.dataset_dir, dp, micro_arch)
            role_desc = RoleDescriptor.from_csv(micro_arch_dir)
            project_name = Path(path.join(micro_arch_dir, 'project.txt')).read_text()
            for r in role_desc.roleEntries:
                source_file_path = resolve_source_file_path(config, r, project_name)
                if source_file_path == None:
                    continue
                shutil.copyfile(source_file_path[0], f"{path.join(micro_arch_dir, source_file_path[1])}.java")       


if __name__ == '__main__':
    #config = parse_args()
    config = Config.default() 
    try:
        logging.basicConfig(level='INFO')
        unzip_source_files(config)
        extract_metadata_from_source(config)
        move_source_files(config)
    except Exception as e:
        logging.error(e)
    finally:
        config.clean_up()
