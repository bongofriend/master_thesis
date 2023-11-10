import argparse
from os import path, mkdir, listdir
import logging
import tempfile
import shutil
import zipfile
import sys
from xml.dom.minidom import Element, parse
from typing import List

class Config:
    source_files_archive_dir: str
    dataset_dir: str
    __tmpDir: tempfile.TemporaryDirectory
    source_dir: str

    def __init__(self, source_files_archive_dir: str, dataset_dir: str):
        current_location = path.dirname(path.realpath(sys.argv[0]))
        self.source_files_archive_dir = path.join(current_location, source_files_archive_dir)
        self.dataset_dir = path.join(current_location, dataset_dir)
        
        self.__tmpDir = tempfile.TemporaryDirectory()
        self.source_dir = path.join(self.get_tmp_dir(), 'sources')
    
    def get_tmp_dir(self) -> str:
        return self.__tmpDir.name
    
    def clean_up(self):
        self.__tmpDir.cleanup()


class RoleEntry:
    role: str
    role_kind: str
    entity: str
        
    def __init__(self, role: str, role_kind: str, entity: str):
        self.role = role
        self.role_kind = role_kind
        self.entity = entity
        
    @staticmethod
    def from_xml(el: Element):
        role = el.tagName
        role_kind = el.getAttribute('roleKind')
        entity_value = ''
        entities = el.getElementsByTagName('entity')
        if len(entities) > 0 and entities[0].firstChild != None:
            entity_value = entities[0].firstChild.nodeValue
        entity = entity_value
        return RoleEntry(role, role_kind, entity)
    
    def to_csv(self, delimiter: str) -> str:
        return delimiter.join([self.role, self.role_kind, self.entity])

class RoleDescriptor:

    roleEntries: List[RoleEntry]

    def __init__(self, roles: [RoleEntry]) -> None:
        self.roleEntries = roles

    @staticmethod
    def from_xml(el: Element):
        role_nodes: List[Element] = []
        role_entries = []
        for e in el.getElementsByTagName('roles'):
            role_nodes.append(e)
        for e in el.getElementsByTagName('actors'):
            role_nodes.append(e)
        for r in role_nodes[0].childNodes:
            if not r.hasChildNodes():
                continue
            for c in r.childNodes:
                if not c.hasChildNodes():
                    continue
                role_entries.append(RoleEntry.from_xml(c))
        return RoleDescriptor(role_entries)
            


    def to_csv(self, delimiter='|') -> str:
        content = [delimiter.join(['role', 'role_kind', 'entity'])]
        content = content + [e.to_csv(delimiter) for e in self.roleEntries]
        return '\n'.join(content)



def parse_args() -> Config:
    parser = argparse.ArgumentParser(
        prog='Unzip and extract files for dataset'
    )
    parser.add_argument('--sourceFilesArchive', type=str, help='Location of archive with source files', dest='archive_dir')
    parser.add_argument('--datasetDir', type=str, help='Directory to where dataset files are to be located', dest='dataset_dir')
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
    with open(path.join(micro_arch_path, 'roles.csv'), mode='w') as f:
        f.write(role_descriptor.to_csv())
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


        


if __name__ == '__main__':
    config = parse_args()
    try:
        logging.basicConfig(level='INFO')
        unzip_source_files(config)
        extract_metadata_from_source(config)
    except Exception as e:
        logging.error(e)
    finally:
        config.clean_up()
