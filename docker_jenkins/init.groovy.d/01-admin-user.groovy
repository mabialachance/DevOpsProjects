
import jenkins.model.Jenkins
import hudson.security.HudsonPrivateSecurityRealm
import hudson.security.FullControlOnceLoggedInAuthorizationStrategy
import hudson.model.User

def jenkins = Jenkins.get()

def adminId = System.getenv("JENKINS_ADMIN_ID")
def adminPassword = System.getenv("JENKINS_ADMIN_PASSWORD")

if (!adminId || !adminPassword) {
    throw new IllegalStateException("Missing Jenkins admin credentials")
}

def realm = jenkins.getSecurityRealm()

if (!(realm instanceof HudsonPrivateSecurityRealm)) {
    def newRealm = new HudsonPrivateSecurityRealm(false)

    if (User.getById(adminId, false) == null) {
        newRealm.createAccount(adminId, adminPassword)
    }

    jenkins.setSecurityRealm(newRealm)

    def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
    strategy.setAllowAnonymousRead(false)

    jenkins.setAuthorizationStrategy(strategy)
    jenkins.save()
} else if (User.getById(adminId, false) == null) {
    realm.createAccount(adminId, adminPassword)
    jenkins.save()
}
