# udc-jobdsl

For using this framework you should disable jobDSL script security. For this you can use the snippet:

```
import javaposse.jobdsl.plugin.GlobalJobDslSecurityConfiguration
import jenkins.model.GlobalConfiguration
println "--> disabling scripts security for jobDSL scripts"
GlobalConfiguration.all().get(GlobalJobDslSecurityConfiguration.class).useScriptSecurity=false
```

Also, you can add this script to init.groovy.d scirpt for automatization
