def build = Thread.currentThread().executable
def endpoint = build.buildVariableResolver.resolve('ENDPOINT')
def version = build.buildVariableResolver.resolve('VERSION')


def output = ''

output += "ENDPOINT=$endpoint<br>"
output += "VERSION=$version<br>"

build.setDescription(output)