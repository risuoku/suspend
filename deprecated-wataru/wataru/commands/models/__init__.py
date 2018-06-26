from . import project
from . import scenario
from . import material
from . import db
from . import console
from . import tag


# attach models tree

tree = {
    'project': {
        'create': project.Create(),
    },
    'scenario': {
        'materialize': scenario.Materialize(),
        'train': scenario.Train(),
        'ls': scenario.Ls(),
        'sync': scenario.Sync(),
        'rm': scenario.Rm(),
    },
    'material': {
        'ls': material.Ls(),
        'train': material.Train(),
        'rm': material.Rm(),
        'inspect': material.Inspect(),
    },
    'db': {
        'setup': db.Setup(),
    },
    'console': console.Console(),
    'tag': tag.Tag(),
}
