def build = Thread.currentThread().executable
def endpoint = build.getEnvironment(listener).get('ENDPOINT')
def version = build.getEnvironment(listener).get('VERSION')


def output = ''

output += "ENDPOINT=$endpoint<br>"
output += "VERSION=$version<br>"

build.setDescription(output)
