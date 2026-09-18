package com.oinkvalley.user_profile_svc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.security.AuthenticationProcessInterceptor;
import org.springframework.grpc.server.security.GrpcSecurity;

/** ClusterIP 내부 Profile gRPC만 익명 허용. HTTP 보안 설정과 분리한다. */
@Configuration
public class GrpcSecurityConfig {

	@Bean
	@GlobalServerInterceptor
	AuthenticationProcessInterceptor grpcSecurityFilterChain(GrpcSecurity grpc) throws Exception {
		return grpc
				.authorizeRequests(requests -> requests.allRequests().permitAll())
				.build();
	}
}
