import argparse
import csv
from dataclasses import asdict, dataclass, fields
import logging
import os
import pathlib
import shutil
from xml.dom.minidom import Element, parse
from typing import List
import zipfile

class RoleEntry:
    role: str
    role_kind: str
    entity: str

    def __init__(self, role: str, role_kind: str, entity: str):
        self.role = role.strip()
        self.role_kind = role_kind.strip()
        self.entity = entity.strip()

    @classmethod
    def from_xml(cls, el: Element) -> 'RoleEntry':
        role = el.tagName
        role_kind = el.getAttribute('rolekind') if el.hasAttribute(
            'rolekind') else el.getAttribute('roleKind')
        entity_value = ''
        entities = el.getElementsByTagName('entity')
        if len(entities) > 0 and entities[0].firstChild != None:
            entity_value = entities[0].firstChild.nodeValue
        entity = entity_value
        return cls(role, role_kind, entity)

    def to_csv(self, writer) -> str:
        return writer.writerow([self.role, self.role_kind, self.entity])


class RoleDescriptor:
    roleEntries: List[RoleEntry]

    def __init__(self, roles: List[RoleEntry]) -> None:
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


@dataclass
class Args:
    psmart_xml_path: pathlib.Path
    output_csv_path: pathlib.Path
    projects_zipfile_path: pathlib.Path

    @staticmethod
    def default() -> "Args":
        return Args(
            pathlib.Path('/home/memi/Dokumente/master_thesis/project/psmart.xml').absolute(),
            pathlib.Path('/home/memi/Dokumente/master_thesis/project/roles.csv').absolute(),
            pathlib.Path('/home/memi/Dokumente/master_thesis/project/source_files.zip').absolute()
        )


@dataclass
class MicroArchNode:
    project: str
    micro_arch: str
    design_pattern: str

    role: str
    role_kind: str
    entity: str


def parse_args() -> Args:
    arg_parser = argparse.ArgumentParser()
    
    arg_parser.add_argument('--psmartXMLPath', '-p', type=str, dest='psmart_xml_path', required=True)
    arg_parser.add_argument('--outputCSVPath', '-o', type=str, dest='output_csv_path', required=True)

    args = arg_parser.parse_args()
    return Args(
        pathlib.Path(args.psmart_xml_path).absolute(), 
        pathlib.Path(args.output_csv_path).absolute()
    )


def parse_micro_arch_node(micro_arch_node: Element, project_name: str, pattern_name: str):
    arch_number = micro_arch_node.getAttribute('number')
    micro_arch_name = f'micro_arch_{arch_number}'
    roles = RoleDescriptor.from_xml(micro_arch_node).roleEntries

    for role in roles:
      if not role:
          continue
      yield MicroArchNode(
            project_name,
            micro_arch_name,
            pattern_name,
            role.role,
            role.role_kind,
            role.entity
        )



def parse_pattern_node(project_name: str, pattern_node: Element):
    pattern_name = pattern_node.getAttribute('name')
    logging.info(f'Extracting {pattern_name} from {project_name}')
    for micro_arch_node in pattern_node.getElementsByTagName('microArchitecture'):
        if not micro_arch_node:
            continue
        return parse_micro_arch_node(micro_arch_node, project_name, pattern_name)


def parse_micro_architectures(xmlPath: pathlib.Path):
     if not xmlPath.exists():
        raise FileNotFoundError()
     with open(xmlPath, mode='r') as xml_content:
        document = parse(xml_content)
        for programNode in document.getElementsByTagName('program'):
            project_name = programNode.getElementsByTagName(
                'name')[0].firstChild.nodeValue
            logging.info(
                f'Extracting design patterns and roles for {project_name}')
            for pattern_node in programNode.getElementsByTagName('designPattern'):
               if not (n := parse_pattern_node(project_name, pattern_node)):
                    continue
               yield from n
         

def parse_psmart_xml(args: Args):
    with open(args.output_csv_path, mode='w') as f:
        header = [f.name for f in fields(MicroArchNode)]
        csv_writer = csv.DictWriter(f, header)
        csv_writer.writeheader()
        for micro_arch_dict in parse_micro_architectures(args.psmart_xml_path):
            csv_writer.writerow(asdict(micro_arch_dict))



def unzip_source_files(args: Args):
    logging.info('Extracting source files...')
    source_file_path = pathlib.Path(os.path.dirname(os.path.abspath(__file__))).joinpath('source_files')
    if source_file_path.exists() and source_file_path.is_dir():
        shutil.rmtree(source_file_path)

    with zipfile.ZipFile(args.projects_zipfile_path, mode='r') as zip:
        zip.extractall(source_file_path)


if __name__ == '__main__':
    args = Args.default()
    try:
        logging.basicConfig(level='INFO')
        unzip_source_files(args)
        parse_psmart_xml(args)
    except Exception as e:
        logging.error(e)