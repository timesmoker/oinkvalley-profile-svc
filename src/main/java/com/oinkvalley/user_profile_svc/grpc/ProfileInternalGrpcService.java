package com.oinkvalley.user_profile_svc.grpc;

import com.oinkvalley.profile.v1.CreateProfileRequest;
import com.oinkvalley.profile.v1.CreateProfileResponse;
import com.oinkvalley.profile.v1.ExistsNicknameRequest;
import com.oinkvalley.profile.v1.ExistsNicknameResponse;
import com.oinkvalley.profile.v1.ProfileInternalServiceGrpc;
import com.oinkvalley.user_profile_svc.dto.profile.CreateUserProfileRequest;
import com.oinkvalley.user_profile_svc.service.UserProfileService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** 기존 {@code /internal/profiles*} 대체. Service 레이어는 그대로 호출. */
@GrpcService
@RequiredArgsConstructor
public class ProfileInternalGrpcService extends ProfileInternalServiceGrpc.ProfileInternalServiceImplBase {

	private final UserProfileService userProfileService;

	@Override
	public void createProfile(CreateProfileRequest request, StreamObserver<CreateProfileResponse> responseObserver) {
		try {
			userProfileService.createProfile(
					new CreateUserProfileRequest(request.getUserId(), request.getNickname()));
			responseObserver.onNext(CreateProfileResponse.getDefaultInstance());
			responseObserver.onCompleted();
		} catch (ResponseStatusException e) {
			responseObserver.onError(toGrpcStatus(e).withDescription(e.getReason()).asRuntimeException());
		} catch (Exception e) {
			responseObserver.onError(
					Status.INTERNAL.withDescription(e.getMessage()).withCause(e).asRuntimeException());
		}
	}

	@Override
	public void existsNickname(
			ExistsNicknameRequest request, StreamObserver<ExistsNicknameResponse> responseObserver) {
		try {
			boolean exists = userProfileService.existsNickname(request.getNickname());
			responseObserver.onNext(ExistsNicknameResponse.newBuilder().setExists(exists).build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onError(
					Status.INTERNAL.withDescription(e.getMessage()).withCause(e).asRuntimeException());
		}
	}

	private static Status toGrpcStatus(ResponseStatusException e) {
		if (e.getStatusCode() instanceof HttpStatus http) {
			return switch (http) {
				case BAD_REQUEST -> Status.INVALID_ARGUMENT;
				case NOT_FOUND -> Status.NOT_FOUND;
				case FORBIDDEN -> Status.PERMISSION_DENIED;
				case CONFLICT -> Status.ALREADY_EXISTS;
				default -> Status.INTERNAL;
			};
		}
		return Status.INTERNAL;
	}
}
