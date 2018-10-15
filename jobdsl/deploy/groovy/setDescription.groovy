def build = Thread.currentThread().executable
def endpoint = build.buildVariableResolver.resolve('ENDPOINT')
build.setDescription("ENDPOINT=$endpoint")
