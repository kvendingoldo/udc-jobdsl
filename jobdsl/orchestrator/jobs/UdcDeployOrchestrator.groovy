package jobdsl.orchestrator.jobs

import com.kvendingoldo.jdcl.core.Functions

class UDC_Deploy_Orchestrator {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            logRotator(jobConfig.job.daysToKeepBuilds,
                       jobConfig.job.maxOfBuildsToKeep)
            wrappers {
                colorizeOutput()
                timestamps()
                preBuildCleanup()
            }
            parameters {
              stringParam{
                  name('VERSION')
                  defaultValue(jobConfig.project.appVersion)
                  description('Version of udc-petclinic project')
                  trim(true)
              }
            }
            steps {
              buildNameUpdater {
                  fromFile(false)
                  buildName('${VERSION}')
                  fromMacro(false)
                  macroTemplate('')
                  macroFirst(false)
              }
            }
            publishers {
              downstreamParameterized {
                  trigger('../Deploy/UDC_Destroy') {
                      parameters {
                          predefinedProp('VERSION', '${VERSION}')
                      }
                  }
                  trigger('../Deploy/UDC_Deploy') {
                      parameters {
                          predefinedProp('VERSION', '${VERSION}')
                      }
                  }
              }
            }
        }
    }
}
