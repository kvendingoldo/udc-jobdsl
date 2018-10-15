package jobdsl.deploy.jobs

import com.kvendingoldo.jdcl.core.Functions

class UdcDeploy {
    static job(dslFactory, jobConfig) {
        dslFactory.job(Functions.generateJobName(jobConfig)) {
            description(jobConfig.job.description)
            label(jobConfig.job.label)
            logRotator(jobConfig.job.daysToKeepBuilds, jobConfig.job.maxOfBuildsToKeep)
            parameters {
                stringParam('VERSION', '', '')
                stringParam('RELEASE_NAME', jobConfig.job.releaseName, '')
                stringParam('KUBERNETES_BRANCH', 'master', '')
            }
            environmentVariables {
                env('GCP_REGION', jobConfig.job.gcp.cloud.region)
                overrideBuildParameters(true)
            }
            scm {
                git {
                    remote {
                        credentials(jobConfig.job.credentials.github)
                        github(jobConfig.job.repository, 'ssh')
                    }
                    branch('refs/heads/${KUBERNETES_BRANCH}')
                    extensions {
                        wipeOutWorkspace()
                        submoduleOptions {
                            recursive()
                        }
                    }
                }
            }
            wrappers {
                kubectlBuildWrapper {
                  serverUrl(jobConfig.job.kubernetes.endpoint)
                  credentialsId(jobConfig.job.credentials.kubernetes)
                  caCertificate('')
                }
                preBuildCleanup()
                colorizeOutput()
            }
            steps {
                shell(dslFactory.readFileFromWorkspace(jobConfig.job.shellScript))
                envInjectBuilder {
                    propertiesFilePath('variables.txt')
                    propertiesContent('')
                }
                buildNameUpdater {
                    fromFile(false)
                    buildName('${RELEASE_NAME}')
                    fromMacro(true)
                    macroTemplate('${RELEASE_NAME}')
                    macroFirst(false)
                }
            }
        }
    }
}
