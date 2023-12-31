from typing import List
from xml.dom.minidom import Element
from os import path
import csv


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

    @classmethod
    def from_csv(cls, micro_arch_path: str, delimiter: str = '|') -> 'RoleDescriptor':
        roles: List[RoleEntry] = []
        with open(path.join(micro_arch_path, 'roles.csv'), mode='r') as f:
            reader = list(csv.DictReader(f, delimiter=delimiter))
            for e in reader:
                roles.append(RoleEntry(e['role'], e['role_kind'], e['entity']))
        return cls(roles)

    def to_csv(self, file_path: str, delimiter='|'):
        with open(file_path, mode="w") as f:
            writer = csv.writer(f, delimiter=delimiter)
            writer.writerow(['role', 'role_kind', 'entity'])
            [r.to_csv(writer) for r in self.roleEntries]
