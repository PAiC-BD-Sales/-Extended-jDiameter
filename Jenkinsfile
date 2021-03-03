pipeline {
	agent any

	tools {
		maven 'Maven_3.6.3'
	}
	
	options {
    	buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '30', numToKeepStr: '10'))
  	}

 
	parameters { string(name: 'EXT_DIAMETER_MAJOR_VERSION_NUMBER', defaultValue: '1.7.3', description: 'The major version for Extended-jDiameter') }

	stages {
		stage("SCM Checkout") {
			steps {
				git credentialsId: 'Fernando', url: 'https://fernando-mendioroz@bitbucket.org/paicdb/extended-jdiameter.git'
			}
			
		}

		stage("Build") {
			steps {
				echo "Building application..."
				script {
                    currentBuild.displayName = "#${params.EXT_DIAMETER_MAJOR_VERSION_NUMBER}-${BUILD_NUMBER}"
                    currentBuild.description = "PAiC Extended-jDiameter"
                }
				sh "mvn clean install -DskipTests"
				echo "Maven build completed."
			}
		}
    	stage('Set Version'){
      		steps{
				sh "mvn versions:set -DnewVersion=${params.EXT_DIAMETER_MAJOR_VERSION_NUMBER}-${BUILD_NUMBER} clean install -DskipTests"
      		}
    	}
		stage("Release") {
			when {
				anyOf {
					branch 'master';
					branch 'release'
				}
			}
			steps {
				echo "Building a release version of #${params.EXT_DIAMETER_MAJOR_VERSION_NUMBER}-${BUILD_NUMBER}"
        		withAnt(installation: 'Ant1.10') {
          			dir('release') {
            			sh "ant -f build.xml -Ddiameter.release.version=${params.EXT_DIAMETER_MAJOR_VERSION_NUMBER}-${BUILD_NUMBER}"
 					}
				}
				echo "Building a release version completed."
			}
		}

		stage('Save Artifacts') {
			when {
				anyOf {
					branch 'master';
					branch 'release'
				}
				expression {
            		currentBuild.result == null || currentBuild.result == 'SUCCESS' 
          		}
			}
        	steps {
          		echo "Archiving Extended jDiameter version ${params.EXT_DIAMETER_MAJOR_VERSION_NUMBER}-${BUILD_NUMBER}"
            	archiveArtifacts artifacts: "release/*.zip", followSymlinks: false, onlyIfSuccessful: true
        	}
    	}
    	stage('Clean workspace') {
			when {
				anyOf {
					branch 'master';
					branch 'release'
				}
			}
			steps {
				sh 'rm -rf release/checkout'
      	        sh 'rm -rf release/target'
			}
		}
  	}

	post {
		success {
			echo "Successfully built"
		}
		failure {
			echo "Building Extended jDiameter failed."
		}
		always {
			echo "This will be called always. After testing do clean up."
		}
	}
}
