package com.example.demo;

import com.example.demo.Status;
import com.example.demo.Parcel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ParcelDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SaveRequest {
        private String sender;
        private String receiver;
        private String destination;
        private String content;

        public Parcel toEntity() {
            return Parcel.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .destination(destination)
                    .content(content)
                    .status(Status.PENDING) // 처음 생성 시 상태는 '접수 대기'
                    .build();
        }
    }

    @Getter
    public static class Response {
        private final Long id;
        private final String sender;
        private final String receiver;
        private final String destination;
        private final String content;
        private final Status status;

        public Response(Parcel parcel) {
            this.id = parcel.getId();
            this.sender = parcel.getSender();
            this.receiver = parcel.getReceiver();
            this.destination = parcel.getDestination();
            this.content = parcel.getContent();
            this.status = parcel.getStatus();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateRequest {
        private Status status;
    }
}