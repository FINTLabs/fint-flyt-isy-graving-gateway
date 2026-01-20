create table dispatch_context
(
    id                               varchar(255) not null,
    source_application_integration_id varchar(255) not null,
    source_application_instance_id    varchar(255) not null,
    tenant                           varchar(255) not null,
    case_id                          varchar(255) not null,
    case_archive_guid                varchar(255) not null,
    callback_url                     varchar(1024) not null,
    primary key (id)
);

create table dispatch_receipt
(
    id                               varchar(255) not null,
    source_application_integration_id varchar(255) not null,
    source_application_instance_id    varchar(255) not null,
    callback_url                     varchar(1024) not null,
    payload                          text,
    primary key (id)
);
