from typing import List
from xml.dom.minidom import Element
from os import path
import csv


class RoleEntry:
    role: str
    role_kind: str
    entity: str
        
    def __init__(self, role: str, role_kind: str, entity: str):
        self.role = role
        self.role_kind = role_kind
        self.entity = entity
        
    @staticmethod
    def from_xml(el: Element) -> 'RoleEntry':
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
            
    @staticmethod
    def from_csv(micrp_arch_path: str, delimiter: str = '|') -> 'RoleDescriptor':
        roles: List[RoleEntry] = []
        with open(path.join(micrp_arch_path, 'roles.csv'), mode='r') as f:
            reader = list(csv.DictReader(f, delimiter=delimiter))
            for e in reader:
                roles.append(RoleEntry(e['role'], e['role_kind'], e['entity']))
        return RoleDescriptor(roles)



    def to_csv(self, delimiter='|') -> str:
        content = [delimiter.join(['role', 'role_kind', 'entity'])]
        content = content + [e.to_csv(delimiter) for e in self.roleEntries]
        return '\n'.join(content)