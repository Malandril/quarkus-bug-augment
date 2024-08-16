package org.acme

import io.quarkus.logging.Log
import io.quarkus.security.identity.AuthenticationRequestContext
import io.quarkus.security.identity.IdentityProvider
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.SecurityIdentityAugmentor
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest
import io.quarkus.security.runtime.QuarkusPrincipal
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.annotation.Priority
import jakarta.inject.Singleton
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/hello")
class GreetingResource(val securityIdentity: SecurityIdentity) {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello(): String {
        return "Hello from Quarkus REST ${securityIdentity.principal.name}"
    }
}

@Singleton
@Priority(5)
class Id1 : IdentityProvider<UsernamePasswordAuthenticationRequest> {
    override fun getRequestType(): Class<UsernamePasswordAuthenticationRequest> {
        return UsernamePasswordAuthenticationRequest::class.java
    }

    override fun authenticate(p0: UsernamePasswordAuthenticationRequest?, p1: AuthenticationRequestContext?): Uni<SecurityIdentity> {
        Log.info("donothing")
        return Uni.createFrom().nullItem()
    }
}

@Singleton
@Priority(4)
class Id2 : IdentityProvider<UsernamePasswordAuthenticationRequest> {
    override fun getRequestType(): Class<UsernamePasswordAuthenticationRequest> {
        return UsernamePasswordAuthenticationRequest::class.java
    }

    override fun authenticate(p0: UsernamePasswordAuthenticationRequest?, p1: AuthenticationRequestContext?): Uni<SecurityIdentity> {
        Log.info("do")
        return Uni.createFrom().item(QuarkusSecurityIdentity.builder().setPrincipal(QuarkusPrincipal("test")).build())
    }
}

@Singleton
class Aug : SecurityIdentityAugmentor {
    override fun augment(identity: SecurityIdentity, context: AuthenticationRequestContext): Uni<SecurityIdentity> {
        Log.info("Augmenting ${identity.principal.name}")
        return Uni.createFrom().item(QuarkusSecurityIdentity.builder(identity).build());
    }
}