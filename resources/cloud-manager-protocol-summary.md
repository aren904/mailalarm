# Cloud Manager Protocol Summary

## changelog

- 2021-12-30 Streamer 8.0
  - 命名规则更新
  - 枚举重新定义
- 2021-12-15
  - 更新命令
    - `Command_GetPublicKey = 7;`
  - 新增命令
    - `Command_None = 0;`
    - `Command_QueryBackupSpaceEnough = 605;`
  - 新增错误码
    - `ErrorCode_QueryBackupSpaceEnoughFailed = 10605;`
  - 更新字段名
    - 字符串类型的字段名 `*_id` 更新为 `*_uuid`
  - 更新字段类型
    - 无符号整型更新为对应的有符号整型
  - 删除字段 `required bool placeholder = 1;  // 防止结构为空` 并重新排序字段序号
- 2020-05-11
  - 追加7.0V1中OSS相关内容
  - 操作重新排序
- 2019-04-04 更新文档
- 2019-01-24 定初稿

## 约定

### 类型

- 时间戳基于秒
- 容量大小基于字节

### 命名

protobuf 的字段名不以 `has_` 作为前缀。

```
命名约定：
   SRC           ->                    源/生产端
   DR            ->                    容灾/容灾端
   VMware Virtual machine ->           VmwareVm
   MDB           ->  metadabase
   RAC Backup    ->  RAC Database  ->  RAC 数据库备份关系
   RDS Backup    ->  RDS Instance  ->  RDS 实例备份关系
   ECS Backup    ->  ECS Instance  ->  ECS 实例备份关系
   OSS Backup    ->                    OSS 备份关系
   MDB Backup    ->                    MDB 备份关系
   DR XX Backup  ->                    XX 容灾关系
   xxxXxxx       ->                    事务型操作
协议中涉及的磁盘 UUID 都为源盘 UUID
```

事务型操作以 X 结尾。比如添加客户端或备份关系或容灾关系，如果 UUID 需要 streamer 服务生成，则不属于事务型操作；如果 UUID 是原先就有的，则属于事务型操作。对于后者需要先在 scmp 创建事务并验证是否存在等条件。

涉及移交用户资源的（部分？）操作以 Ext 结尾。

### 取值

对于整型字段类似 xx_id，>0 表示某个具体的对象；=0 表示不存在的或者特殊的一个对象。比如 group_id = 1 表示一个 id = 1 的组；group_id = 0 表示一个不存在的组。

### 客户端和备份关系和容灾关系

| 客户端        | 备份关系/容灾关系对应的载体 | 允许一个关系包含多载体 | 备份空间             | 客户端全局唯一 |
| ------------- | --------------------------- | ---------------------- | -------------------- | -------------- |
| Single        | 磁盘                        | 是                     | 独享                 | 是             |
| MSCS          | 磁盘                        | 是                     | 独享                 | 是             |
| FileSingle    | 磁盘                        | 是                     | 独享                 | 是             |
| AIX           | 磁盘                        | 是                     | 独享                 | 是             |
| StorageClient | 磁盘                        | 是                     | 独享                 | 是             |
| vCenter       | 虚拟机                      | 否                     | 独享                 | 否             |
| RAC           | 数据库                      | 否                     | 共享 Oracle 备份空间 | 否             |
| ECS           | 实例                        | 否                     | 共享云备份空间       | 否             |
| OSS           | 备份                        | 否                     | 共享云备份空间       | 否             |
| RDS           | 实例                        | 否                     | 共享云备份空间       | 否             |
| MDB           | 备份                        | 否                     | 共享云备份空间       | 否             |

> 注意
>
> 协议的 request 中 backups 需要根据上述表中客户端类型和是否允许一个关系包含多载体分别处理。

### 备份关系/容灾关系容量

- 对于独享备份空间的关系，比如块级备份

  容量表示该关系的总容量大小。如果保存卷支持扩容，则用于备份的总容量大小为镜像卷大小 + 最大扩容大小；如果保存卷不支持扩容，则用于备份的总容量大小为镜像卷大小 + 保存卷容量。

- 共享 Oracle 备份空间的关系，比如 RAC

  配置关系时指定的最大可用空间。

- 共享云备份空间

  不支持创建关系时指定大小，不支持更新关系的大小。关系的当前大小为实际已经创建的备份点大小 + 正在创建的备份点大小。

### 版本

如果更新协议导致无法兼容以前的协议，那么需要更新版本。

## 通用定义

### 消息头

| 偏移 （Byte） | 长度（Byte） | 描述                       | 类型  | 类型                                  |
| ------------- | ------------ | -------------------------- | ----- | ------------------------------------- |
| 0             | 1            | 版本                       | byte  | protobuf 里的 Version 枚举类型        |
| 1             | 1            | 数据类型，消息体的数据类型 | byte  | [DataType](#DataType)                 |
| 2             | 2            | 方向                       | int16 | [MessageDirection](#MessageDirection) |
| 4             | 4            | 命令                       | int32 | [Command](#Command)                   |
| 8             | 4            | 错误码                     | int32 | [ErrorCode](#ErrorCode)               |
| 12            | 4            | 数据长度，消息体数据长度   | int32 | >= 0                                  |
| 16            | 8            | flags1（目前控制台使用）   | int64 |                                       |

> 注意
>
> 消息头中消息体数据长度为正数时表示有消息体；为 0 时，如果是请求或者是响应并且错误码为 0 时，如果协议定义了 request 或者 response ，则需要考虑 protobuf 把所有字段都为默认值的消息序列化成 0 字节的情况，在处理 request 或者 response 需要手动创建对象。

### 枚举

> 注意
>
> 字段名根据各自语言规范重新命名。

#### DataType

```c++
enum class DataType : uint8_t {
  None = 0,
  Protobuf = 1,
  Binary = 2,
};
```

#### MessageDirection

```c++
enum class MessageDirection : uint16_t {
  None = 0,
  ConsoleToCloudManager = 1,
  CloudManagerToConsole = 2,
  AlarmToCloudManager = 3,
  CloudManagerToAlarm = 4,
  StreamerToCloudManager = 5,
  CloudManagerToStreamer = 6,
};
```

#### Command

```c++
enum class Command : int32_t {
  None = 0,

  // login begin

  LoginCloudManager = 1000,
  ForceLoginCloudManager = 1001,
  LogoutCloudManager = 1002,
  VerifyUser = 1003,
  ConsoleHeartbeatsToCloudManager = 1004,

  // login end

  // user group begin

  CreateUserGroup = 1101,
  DeleteUserGroup = 1102,
  UpdateUserGroup = 1103,
  MigrateUserGroup = 1104,

  // user group end

  // user begin

  GetUsers = 1200,
  CreateUser = 1201,
  DeleteUser = 1202,
  UpdateUser = 1203,
  UpdateUserPassword = 1204,
  ResetUserPassword = 1205,
  MigrateUser = 1206,

  // user end

  // cloud manager begin

  GetCloudManagers = 1300,
  AddCloudManager = 1301,
  UpdateCloudManager = 1302,
  RemoveCloudManager = 1303,
  VerifyCloudManager = 1304,

  // cloud manager end

  // data ark group begin

  CreateDataArkGroup = 1401,
  DeleteDataArkGroup = 1402,
  UpdateDataArkGroup = 1403,

  // data ark group end

  // data ark begin

  GetDataArks = 1500,
  AddDataArk = 1501,
  RemoveDataArk = 1502,
  RemoveDataArkX = 1503,
  ForceRemoveDataArk = 1504,
  UpdateDataArk = 1505,
  MigrateDataArk = 1506,
  UpdateDataArkRole = 1507,
  GetDataArkResource = 1508,
  GetDataArkDrs = 1509,
  CreateDataArkDrX = 1510,
  CreateDataArkDrExtX = 1511,
  DeleteDataArkDrX = 1512,

  // data ark end

  // quota begin

  GetUserQuota = 1600,
  CreateUserQuotaX = 1601,
  DeleteUserQuotaX = 1602,
  UpdateUserQuotaX = 1603,
  GetUserResourceForTransfer = 1604,
  TransferUserResource = 1605,
  QueryBackupSpaceEnough = 1606,

  // quota end

  // client group begin

  CreateClientGroup = 1700,
  DeleteClientGroup = 1701,
  UpdateClientGroup = 1702,

  // client group end

  // client begin

  GetClients = 1800,
  MigrateClient = 1801,
  AddClient = 1802,
  AddClientExtX = 1803,
  RemoveClientX = 1804,
  UpdateClientAkskX = 1805,

  // client end

  // client backup begin

  CreateClientBackup = 1900,
  CreateClientBackupX = 1901,
  DeleteClientBackupX = 1902,
  UpdateClientBackupX = 1903,
  CreateDrClientBackupX = 1904,
  DeleteDrClientBackupX = 1905,
  UpdateDrClientBackupX = 1906,

  // client backup end

  // overview begin

  GetOverview = 2000,
  GetOverviewDataArks = 2001,
  GetOverviewClients = 2002,
  GetOverviewVmwareVms = 2003,
  GetOverviewCloudHost = 2004,
  GetUserReport = 2005,
  GetDataArkReport = 2006,
  GetBackupReport = 2007,

  // overview end

  // alarm log begin

  GetAlarmLogs = 2100,
  DeleteAlarmLog = 2101,
  UpdateAlarmLogProcessed = 2102,

  // alarm log end

  // operation log begin

  GetOperationLogs = 2200,
  CreateOperationLog = 2201,

  // operation log end

  // system - email alarm begin

  GetEmailAlarm = 2300,
  CreateEmailAlarm = 2301,
  DeleteEmailAlarm = 2302,
  UpdateEmailAlarm = 2303,
  VerifyEmailAlarm = 2304,

  // system - email alarm end

  // system - snmp begin

  GetSnmp = 2400,
  UpdateSnmp = 2401,

  // system - snmp end

  // system - cloud service endpoint begin

  GetCloudServiceEndpoint = 2500,
  UpdateCloudServiceEndpoint = 2501,

  // system - cloud service endpoint end

  // transaction begin

  CommitTransaction = 2601,
  RollbackTransaction = 2602,

  // transaction end

  // fake begin
  // 仅用于记录操作日志，不作为接口指令

  FakeManageDataArk = 5001,
  FakeUpdateDataArkGroupCloudManager = 5002,
  FakeCreateDataArkGroupDataCenter = 5003,
  FakeDeleteDataArkGroupDataCenter = 5004,
  FakeUpdateDataArkGroupDataCenter = 5005,
  FakeCreateDataArkGroupResourcePool = 5006,
  FakeDeleteDataArkGroupResourcePool = 5007,
  FakeUpdateDataArkGroupResourcePool = 5008,
  FakeUpdateAlarmLogProcessed = 5009,
  FakeUpdateAlarmLogUnprocessed = 5010,
  FakeExportDataArkLog = 5011,
  FakeExportDataArkReport = 5012,
  FakeExportUserReport = 5013,
  FakeExportBackupReport = 5014,
  FakeUpdateEcsEndpoint = 5015,
  FakeUpdateOssEndpoint = 5016,
  FakeUpdateRdsEndpoint = 5017,

  // fake end
};
```

#### ErrorCode

```c++
// general errors [1, 999]
// global errors [1000, 1999]
// scmp errors [2000, 9999]
// operation errors [10000+first_command, 19999]
enum class ErrorCode : int32 {
  Success = 0,

  // general errors begin

  Failed = 1,
  FileExists = 2,
  FileNotFound = 3,
  FileCreateFailed = 4,
  FileRemoveFailed = 5,
  FileOpenFailed = 6,
  FileReadFailed = 7,
  FileWriteFailed = 8,
  DirectoryExists = 9,
  DirectoryNotFound = 10,
  DirectoryCreateFailed = 11,
  DirectoryRemoveFailed = 12,

  // general errors end

  // global errors begin

  // 未识别的操作
  UnrecognizedCommand = 1000,
  // 不支持的操作
  OperationNotSupported = 1001,
  // 无效的操作
  InvalidOperation = 1002,
  // 过多请求
  TooManyRequests = 1003,
  // 序列化失败
  SerializeFailed = 1004,
  // 反序列化失败
  DeserializeFailed = 1005,
  // 没有权限
  PermissionDenied = 1006,
  // 参数错误
  InvalidArgument = 1007,
  // 加密失败
  EncryptFailed = 1008,
  // 解密失败
  DecryptFailed = 1009,
  // 数据库连接失败
  DatabaseConnectFailed = 1010,
  // 数据库查询失败
  DatabaseQueryFailed = 1011,
  // 操作正在执行
  OperationInProgress = 1012,
  // 无效的配置
  InvalidConfig = 1013,
  // 套接字连接错误
  SocketConnectError = 1014,

  // global errors end

  // user group errors begin

  // 用户组存在的
  UserGroupExists = 2000,
  // 用户组不存在的
  UserGroupNotFound = 2001,
  // 用户组重名
  UserGroupSameName = 2002,

  // user group errors end

  // user errors begin

  // 用户存在的
  UserExists = 2100,
  // 用户不存在的
  UserNotFound = 2101,
  // 用户重 UUID
  UserSameUuid = 2102,
  // 用户重(显示)名
  UserSameName = 2103,
  // 用户名或密码不匹配
  UserPasswordUnmatch = 2104,
  // 用户新密码和旧密码一致
  UserNewPasswordSameToOld = 2105,
  // 用户在线（用于限制对在线用户执行操作的场景）
  UserOnline = 2106,
  // 用户已经登录（用于不能重复登录）
  UserAlreadyLoggedIn = 2107,
  // 用户被登出由于在其他地方登录（用于通知当前会话的控制台被登出）
  UserLoggedOutDueToLoggedInElsewhere = 2108,
  // 用户长时间未操作
  UserInteractionTimeout = 2109,

  // user errors end

  // user session errors begin

  // 用户会话存在的
  UserSessionExists = 2200,
  // 用户会话不存在的
  UserSessionNotFound = 2201,

  // user session errors end

  // data ark group errors begin

  // 数据方舟组存在的
  DataArkGroupExists = 2300,
  // 数据方舟组不存在的
  DataArkGroupNotFound = 2301,
  // 数据方舟组重名
  DataArkGroupSameName = 2302,

  // data ark group errors end

  // data ark errors begin

  // 数据方舟存在的
  DataArkExists = 2400,
  // 数据方舟不存在的
  DataArkNotFound = 2401,
  // 数据方舟重名
  DataArkSameName = 2402,
  // 容灾数据方舟关系存在的
  DataArkDrExists = 2403,
  // 容灾数据方舟关系不存在的
  DataArkDrNotFound = 2404,

  // data ark errors end

  // quota errors begin

  // 配额存在的（导致删除用户失败等）
  QuotaExists = 2500,
  // 配额不存在的（导致添加客户端失败等）
  QuotaNotFound = 2501,
  // 配额不足（导致添加客户端失败等）
  QuotaInsufficient = 2502,

  // quota errors end

  // client group errors begin

  // 客户端组存在的
  ClientGroupExists = 2600,
  // 客户端组不存在的
  ClientGroupNotFound = 2601,
  // 客户端组重名
  ClientGroupSameName = 2602,

  // client group errors end

  // client errors begin

  // 客户端存在的
  ClientExists = 2700,
  // 客户端不存在的
  ClientNotFound = 2701,
  // 容灾客户端存在的
  DrClientExists = 2702,
  // 容灾客户端不存在的
  DrClientNotFound = 2703,

  // client errors end

  // backup errors begin

  // 备份关系存在的
  BackupExists = 2800,
  // 备份关系不存在的
  BackupNotFound = 2801,
  // 容灾关系存在的
  DrBackupExists = 2802,
  // 容灾关系不存在的
  DrBackupNotFound = 2803,

  // backup errors end

  // system settings errors begin

  // 邮件报警设置存在的
  EmailAlarmExists = 2900,
  // 邮件报警设置不存在的
  EmailAlarmNotFound = 2901,

  // system settings errors end

  // transaction errors begin

  // 存在事务
  TransactionExists = 3000,
  // 不存在事务
  TransactionNotFound = 3001,

  // transaction errors end

  // operation error begin

  // login begin

  LoginCloudManagerFailed = 11000,
  ForceLoginCloudManagerFailed = 11001,
  LogoutCloudManagerFailed = 11002,
  VerifyUserFailed = 11003,
  ConsoleHeartbeatsToCloudManagerFailed = 11004,

  // login end

  // user group begin

  CreateUserGroupFailed = 11101,
  DeleteUserGroupFailed = 11102,
  UpdateUserGroupFailed = 11103,
  MigrateUserGroupFailed = 11104,

  // user group end

  // user begin

  GetUsersFailed = 11200,
  CreateUserFailed = 11201,
  DeleteUserFailed = 11202,
  UpdateUserFailed = 11203,
  UpdateUserPasswordFailed = 11204,
  ResetUserPasswordFailed = 11205,
  MigrateUserFailed = 11206,

  // user end

  // cloud manager begin

  GetCloudManagersFailed = 11300,
  AddCloudManagerFailed = 11301,
  UpdateCloudManagerFailed = 11302,
  RemoveCloudManagerFailed = 11303,
  VerifyCloudManagerFailed = 11304,

  // cloud manager end

  // data ark group begin

  CreateDataArkGroupFailed = 11401,
  DeleteDataArkGroupFailed = 11402,
  UpdateDataArkGroupFailed = 11403,

  // data ark group end

  // data ark begin

  GetDataArksFailed = 11500,
  AddDataArkFailed = 11501,
  RemoveDataArkFailed = 11502,
  RemoveDataArkXFailed = 11503,
  ForceRemoveDataArkFailed = 11504,
  UpdateDataArkFailed = 11505,
  MigrateDataArkFailed = 11506,
  UpdateDataArkRoleFailed = 11507,
  GetDataArkResourceFailed = 11508,
  GetDataArkDrsFailed = 11509,
  CreateDataArkDrXFailed = 11510,
  CreateDataArkDrExtXFailed = 11511,
  DeleteDataArkDrXFailed = 11512,

  // data ark end

  // quota begin

  GetUserQuotaFailed = 11600,
  CreateUserQuotaXFailed = 11601,
  DeleteUserQuotaXFailed = 11602,
  UpdateUserQuotaXFailed = 11603,
  GetUserResourceForTransferFailed = 11604,
  TransferUserResourceFailed = 11605,
  QueryBackupSpaceEnoughFailed = 11606,

  // quota end

  // client group begin

  CreateClientGroupFailed = 11700,
  DeleteClientGroupFailed = 11701,
  UpdateClientGroupFailed = 11702,

  // client group end

  // client begin

  GetClientsFailed = 11800,
  MigrateClientFailed = 11801,
  AddClientFailed = 11802,
  AddClientExtXFailed = 11803,
  RemoveClientXFailed = 11804,
  UpdateClientAkskXFailed = 11805,

  // client end

  // client backup begin

  CreateClientBackupFailed = 11900,
  CreateClientBackupXFailed = 11901,
  DeleteClientBackupXFailed = 11902,
  UpdateClientBackupXFailed = 11903,
  CreateDrClientBackupXFailed = 11904,
  DeleteDrClientBackupXFailed = 11905,
  UpdateDrClientBackupXFailed = 11906,

  // client backup end

  // overview begin

  GetOverviewFailed = 12000,
  GetOverviewDataArksFailed = 12001,
  GetOverviewClientsFailed = 12002,
  GetOverviewVmwareVmsFailed = 12003,
  GetOverviewCloudHostFailed = 12004,
  GetUserReportFailed = 12005,
  GetDataArkReportFailed = 12006,
  GetBackupReportFailed = 12007,

  // overview end

  // alarm log begin

  GetAlarmLogsFailed = 12100,
  DeleteAlarmLogFailed = 12101,
  UpdateAlarmLogProcessedFailed = 12102,

  // alarm log end

  // operation log begin

  GetOperationLogsFailed = 12200,
  CreateOperationLogFailed = 12201,

  // operation log end

  // system - email alarm begin

  GetEmailAlarmFailed = 12300,
  CreateEmailAlarmFailed = 12301,
  DeleteEmailAlarmFailed = 12302,
  UpdateEmailAlarmFailed = 12303,
  VerifyEmailAlarmFailed = 12304,

  // system - email alarm end

  // system - snmp begin

  GetSnmpFailed = 12400,
  UpdateSnmpFailed = 12401,

  // system - snmp end

  // system - cloud service endpoint begin

  GetCloudServiceEndpointFailed = 12500,
  UpdateCloudServiceEndpointFailed = 12501,

  // system - cloud service endpoint end

  // transaction begin

  CommitTransactionFailed = 12601,
  RollbackTransactionFailed = 12602,

  // transaction end

  // no fake error

  // operation error end
};
```

#### Exception

```c++
enum class Exception : int32_t {
  // 正常
  Normal = 0,
  // 备份异常—本地丢失
  ClientLocalLost = 1,
  // 备份异常—目标丢失
  ClientTargetLost = 2,
  // VMware虚拟机任务计划快照点创建失败
  VmwareCreateSnapshotFailed = 3,
  // CBT 异常
  VmwareCbtDrop = 4,
  // 普通客户端离线
  ClientOffline = 5,
  // vCenter 离线
  VcenterOffline = 6,
  // 集群节点部分离线
  ClusterNodeExistOffline = 7,
  // 集群节点全部离线
  ClusterNodeAllOffline = 8,
  // 存储池异常
  StreamerPoolDisabled = 9,
  // Streamer 服务器离线
  StreamerOffline = 10,
  // 自动扩容失败
  ClientAutoExpandFailed = 11,
  // 快照合并失败
  ClientSnapshotMergeFailed = 12,
  // Oracle 备份空间异常
  OracleStorageDrop = 13,
  // 容灾复制失败
  DisasterReplicationFailed = 14,
  // RAC 节点存在离线
  RacNodeExistOffline = 15,
  // RAC 节点全部离线
  RacNodeAllOffline = 16,
  // RAC 实例离线
  RacInstanceExistOffline = 17,
  // RAC 实例全部离线
  RacInstanceAllOffline = 18,
  // Vmware 虚拟机离线
  VmwareVmOffline = 19,
  // 普通客户端创建快照点失败
  ClientCreateSnapshotFailed = 20,
  // RAC 客户端创建快照点失败
  RacCreateSnapshotFailed = 21,
  // 容灾服务离线
  DisasterServerOffline = 22,
  // 存储池超过阈值
  StreamerPoolExceededThreshold = 23,
  // 映射给 AIX 的磁盘离线
  AixDiskOffline = 24,
  // VMware 同步数据失败
  VmwareInitMirrorFailed = 25,
  // 在线虚拟机创建快照点数据异常
  VmwareSnapshotSizeIsZero = 26,
  // 文件级备份客户端任务计划创建快照点失败
  FileSingleCreateSnapshotFailed = 27,
  // 文件级备份客户端离线
  FileSingleOffline = 28,
  // 文件级备份客户端异常-本地丢失
  FileSingleLocalLost = 29,
  // 文件级备份客户端异常-目标丢失
  FileSingleTargetLost = 30,
  // RDS 实例备份点下载失败
  RdsBackupBackupPointDownloadFailed = 31,
  // RDS 实例离线，即源丢失，云上实例离线
  RdsBackupOffline = 32,
  // 7.0V1中已废弃，RDS 备份空间异常
  RdsStorageDrop = 33,
  // RDS 认证异常
  RdsAuthAbnormal = 34,
  // RDS 客户端离线
  RdsClientOffline = 35,
  // 7.0V2中弃用，RDS 备份模块服务异常
  RdsBackupServiceOffline = 36,
  // 云备份空间异常
  CloudStorageDrop = 37,
  // OSS 备份点下载失败
  OssBackupPointDownloadFailed = 38,
  // OSS 认证异常
  OssAuthAbnormal = 39,
  // OSS 客户端离线
  OssClientOffline = 40,
  // 7.0V2 中弃用，OSS 备份模块服务异常
  OssBackupServiceOffline = 41,
  // OSS 备份空间达到阈值
  OssBackupSpaceExceededThreshold = 42,
  // OSS 备份目标丢失，本地目录丢失
  OssBackupDestLost = 43,
  // OSS 备份源丢失，即云上桶或目录丢失
  OssBackupSourceLost = 44,
  // ECS 认证异常
  EcsAuthAbnormal = 45,
  // ECS 客户端离线
  EcsClientOffline = 46,
  // ECS 实例备份点下载失败
  EcsBackupBackupPointDownloadFailed = 47,
  // ECS 备份空间达到阈值
  EcsBackupSpaceExceededThreshold = 48,
  // ECS 备份目标丢失，本地目录丢失
  EcsDestLost = 49,
  // ECS 实例离线，即源丢失，云上实例离线
  EcsBackupOffline = 50,
  // 元数据库认证异常，AKSK/IP/Password 异常
  MetaAuthAbnormal = 51,
  // 元数据库客户端离线
  MetaClientOffline = 52,
  // 元数据库备份点下载失败
  MetaBackupPointDownloadFailed = 53,
  // 元数据库备份空间达到阈值
  MetaBackupSpaceExceededThreshold = 54,
  // 元数据库备份目标丢失，本地目录丢失
  MetaBackupDestLost = 55,
  // 元数据库备份源丢失
  MetaBackupSourceLost = 56,
  // RDS 目标丢失，本地目录丢失
  RdsBackupDestLost = 57,
  // Oracle 备份空间达到阈值
  OracleBackupSpaceExceededThreshold = 58,
  // 代理客户端同步数据失败
  ClientInitMirrorFailed = 59,
  // stmvda 服务离线
  StmvdaServiceOffline = 60,
  // stmvdp 服务离线
  StmvdpServiceOffline = 61,
  // stmrecovery 服务离线
  StmRecoveryServiceOffline = 62,
  // scmp 服务离线
  ScmpServiceOffline = 63,
  // osnsan 服务离线
  OsnsanServiceOffline = 64,
  // osnibrs 服务离线
  OsnibrsServiceOffline = 65,
  // metabackup 服务离线
  MetabackupServiceOffline = 66,
  // ecsbackup 服务离线
  EcsbackupServiceOffline = 67,
};
```

#### ClientType

```c++
enum class ClientType : int32_t {
  Single = 0,
  VmwareVm = 1,
  Mscs = 2,
  Rac = 3,
  Vcenter = 4,
  AIX = 5,
  FileSingle = 11,
  Rds = 12,
  RdsBackup = 13,
  Oss = 14,
  OssBackup = 15,
  Ecs = 16,
  EcsBackup = 17,
  Mdb = 18,
  MdbBackup = 19,
  StorageClient = 20,
  None = 2147483647,
};
```

> 注意
>
> 在本文档的范畴内，我们称 Single、Mscs、Rac、Vcenter、AIX、FileSingle、Rds、Oss、Ecs、Mdb、StorageClient 为客户端，VmwareVm、RdsBackup、OssBackup、EcsBackup、MdbBackup 为备份（关系）。以下操作涉及到的客户端指上述类型的客户端。

> 注意
>
> 由于历史原因，客户端类型 0 表示 Single，故采用 INT32_MAX 表示 None。协议的消息中如果备份结构中类型不可用，则赋值 None，比如 Single 客户端的备份关系；诸如 vCenter 客户端的备份关系的类型为 vCenterVm。

#### CloudServiceEndpointType

```c++
enum class CloudServiceEndpointType : int32_t {
  None = 0,
  Rds = 1,
  Oss = 2,
  Ecs = 3,
};
```

#### DataArkGroupType

```c++
enum class DataArkGroupType : int32_t {
  // 管理平台
  CloudManager = 0,
  // 数据中心
  DataCenter = 1,
  // 资源池
  ResourcePool = 2,
};
```

#### DataArkRole

```c++
enum class DataArkRole : int32_t {
  None = 0,
  Production = 1,
  Disaster = 2,
};
```

#### SnmpVersion

```c++
enum class SnmpVersion : int32_t {
  V1 = 0,
  V2c = 1,
  V3 = 2,
};
```

#### SnmpAuthenticationProtocol

```c++
enum class SnmpAuthenticationProtocol : int32_t {
  HmacMd5 = 0,
  Sha = 1,
};
```

#### SnmpPrivacyProtocol

```c++
enum class SnmpPrivacyProtocol : int32_t {
  Des = 0,
  3Des = 1,
  Aes128 = 2,
  Aes192 = 3,
  Aes256 = 4,
};
```

#### UserRole

```c++
enum class UserRole : int32 {
  None = 0,
  Admin = 1,
  User = 2,
  // 审计员
  Auditor = 3,
  // 只能管理 CloudManager
  Topo = 4,
  Root = 5,
};
```

#### OperationLogResult

```c++
enum class OperationLogResult : int32_t {
  Success = 0,
  Failed = 1,
};
```

## scmp 和 console 的操作

### 登录

#### 登录 Cloud Manager

> user role
>
> - all

- LoginCloudManager = 1000
- console >>> scmp
  - LoginCloudManagerRequest
- scmp >>> console
  - LoginCloudManagerResponse

#### 强制登录 Cloud Manager

> user role
>
> - all

- ForceLoginCloudManager = 1001
- console >>> scmp
  - ForceLoginCloudManagerRequest
- scmp >>> console
  - ForceLoginCloudManagerResponse

#### 登出 Cloud Manager

> user role
>
> - all

- LogoutCloudManager = 1002
- console >>> scmp
  - 无
- scmp >>> console
  - 无

#### 验证用户

> user role
>
> - all

验证当前用户是当前用户

- VerifyUser = 1003
- console >>> scmp
  - VerifyUserRequest
- scmp >>> console
  - 无

#### 心跳给 Cloud Manager

> user role
>
> - all

用于检测网络连接断开或者无响应，用户长时间无操作

- ConsoleHeartbeatsToCloudManager = 1004
- console >>> scmp
  - ConsoleHeartbeatsToCloudManagerRequest
- scmp >>> console
  - 无

### 用户组

#### 创建用户组

> user role
>
> - root
> - admin

- CreateUserGroup = 1101
- console >>> scmp
  - CreateUserGroupRequest
- scmp >>> console
  - 无

#### 删除用户组

> user role
>
> - root
> - admin

- DeleteUserGroup = 1102
- console >>> scmp
  - DeleteUserGroupRequest
- scmp >>> console
  - 无

#### 更新用户组

> user role
>
> - root
> - admin

- UpdateUserGroup = 1103
- console >>> scmp
  - UpdateUserGroupRequest
- scmp >>> console
  - 无

#### 移动用户组

> user role
>
> - root
> - admin

- MigrateUserGroup = 1104
- console >>> scmp
  - MigrateUserGroupRequest
- scmp >>> console
  - 无

### 用户

#### 获取用户

> user role
>
> - all

- GetUsers = 1200
- console >>> scmp
  - 无
- scmp >>> console
  - GetUsersResponse

#### 创建用户

> user role
>
> - root
> - admin

- CreateUser = 1201
- console >>> scmp
  - CreateUserRequest
- scmp >>> console
  - 无

#### 删除用户

> user role
>
> - root
> - admin

- DeleteUser = 1202
- console >>> scmp
  - DeleteUserRequest
- scmp >>> console
  - 无

#### 更新用户信息

> user role
>
> - all
>
> target
>
> - self

- UpdateUser = 1203
- console >>> scmp
  - UpdateUserRequest
- scmp >>> console
  - 无

#### 更新用户密码

> user role
>
> - all
>
> target
>
> - self

- UpdateUserPassword = 1204
- console >>> scmp
  - UpdateUserPasswordRequest
- scmp >>> console
  - 无

#### 重置用户密码

> user role
>
> - root
>   - target user role
>     - admin
>     - auditor
>     - user
> - admin
>   - target user role
>     - auditor
>     - user

- ResetUserPassword = 1205
- console >>> scmp
  - ResetUserPasswordRequest
- scmp >>> console
  - 无

#### 移动用户

> user role
>
> - root
> - admin

- MigrateUser = 1206
- console >>> scmp
  - MigrateUserRequest
- scmp >>> console
  - 无

### 数据方舟组

#### 创建数据方舟组

> user role
>
> - root
> - admin

- CreateDataArkGroup = 1401
- console >>> scmp
  - CreateDataArkGroupRequest
- scmp >>> console
  - 无

#### 删除数据方舟组

> user role
>
> - root
> - admin

- DeleteDataArkGroup = 1402
- console >>> scmp
  - DeleteDataArkGroupRequest
- scmp >>> console
  - 无

#### 更新数据方舟组

> user role
>
> - root
> - admin

- UpdateDataArkGroup = 1403
- console >>> scmp
  - UpdateDataArkGroupRequest
- scmp >>> console
  - 无

### 数据方舟

#### 获取数据方舟

> user role
>
> - all

- GetDataArks = 1500
- console >>> scmp
  - 无
- scmp >>> console
  - GetDataArksResponse

#### 添加数据方舟

> user role
>
> - root
> - admin

- AddDataArk = 1501
- console >>> scmp
  - AddDataArkRequest
- scmp >>> console
  - 无

#### 移除数据方舟

> user role
>
> - root
> - admin

- RemoveDataArkX = 1502
- console >>> scmp
  - RemoveDataArkXRequest
- scmp >>> console
  - RemoveDataArkXResponse

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

#### 更新数据方舟

> user role
>
> - root
> - admin

- UpdateDataArk = 1504
- console >>> scmp
  - UpdateDataArkRequest
- scmp >>> console
  - 无

#### 迁移数据方舟

> user role
>
> - root
> - admin

- MigrateDataArk = 1505
- console >>> scmp
  - MigrateDataArkRequest
- scmp >>> console
  - 无

#### 获取数据方舟资源

数据方舟当前可被分配的配额，用于为用户创建或更新配额时。

> user role
>
> - all

- GetDataArkResource = 1507
- console >>> scmp
  - 无
- scmp >>> console
  - GetDataArkResourceResponse

#### 获取数据方舟容灾关系

> user role
>
> - all

- GetDataArkDrs = 1508
- console >>> scmp
  - GetDataArkDrsRequest
- scmp >>> console
  - GetDataArkDrsResponse

#### 创建数据方舟容灾关系

> user role
>
> - user

- CreateDataArkDrX = 1509
- console >>> scmp
  - CreateDataArkDrXRequest
- scmp >>> console
  - CreateDataArkDrXResponse

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

#### 创建数据方舟容灾关系

> user role
>
> - root
> - admin
>
> target user role
>
> - user

- CreateDataArkDrExtX = 1510
- console >>> scmp
  - CreateDataArkDrExtXRequest
- scmp >>> console
  - CreateDataArkDrExtXResponse

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

#### 删除数据方舟容灾关系

> user role
>
> - user

- DeleteDataArkDrX = 1511
- console >>> scmp
  - DeleteDataArkDrXRequest
- scmp >>> console
  - DeleteDataArkDrXResponse

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

### 配额

#### 获取用户配额

> user role
>
> - all

- GetUserQuota = 1600
- console >>> scmp
  - GetUserQuotaRequest
- scmp >>> console
  - GetUserQuotaResponse

#### 创建用户配额

> user role
>
> - root
> - admin

- CreateUserQuotaX = 1601
- console >>> scmp
  - CreateUserQuotaXRequest
- scmp >>> console
  - CreateUserQuotaXResponse

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

#### 删除用户配额

> user role
>
> - root
> - admin

- DeleteUserQuotaX = 1602
- console >>> scmp
  - DeleteUserQuotaXRequest
- scmp >>> console
  - DeleteUserQuotaXResponse

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

#### 更新用户配额

> user role
>
> - root
> - admin

> 包括创建、更新、删除

- UpdateUserQuotaX = 1603
- console >>> scmp
  - UpdateUserQuotaXRequest
- scmp >>> console
  - UpdateUserQuotaXResponse

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

#### 获取用于移交的用户资源

获取指定用户的客户端及其所属的数据方舟，用于移交用户资源。

> user role
>
> - all

- GetUserResourceForTransfer = 1604
- console >>> scmp
  - GetUserResourceForTransferRequest
- scmp >>> console
  - GetUserResourceForTransferResponse

#### 移交用户资源

包括客户端和配额。

> user role
>
> - root
> - admin
>
> target user user role
>
> - user

> 注意：
>
> - 确保移交时目标用户相关源数据方舟和容灾数据方舟的关系已经存在
> - 确保移交时目标用户相关 vCenter 已经存在
> - 确保移交时目标用户相关 RDS 已经存在
> - 确保移交时目标用户相关 OSS 已经存在
> - 确保移交时目标用户相关 ECS 已经存在

> 注意：
>
> - result 可能返回空，需要判断后再处理
>   - result 为空
>     - 所有数据方舟都不存在错误时
>     - 检查发现参数错误或者数据方舟不存在或者客户端不存
>     - 其他
>   - result 不为空
>     - 检查发现数据方舟存在不存在配额或者配额不足的问题

- TransferUserResource = 1605
- console >>> scmp
  - TransferUserResourceRequest
- scmp >>> console
  - TransferUserResourceResponse

### 客户端组

#### 创建客户端组

> user role
>
> - user

- CreateClientGroup = 1700
- console >>> scmp
  - CreateClientGroupRequest
- scmp >>> console
  - 无

#### 删除客户端组

> user role
>
> - user

- DeleteClientGroup = 1701
- console >>> scmp
  - DeleteClientGroupRequest
- scmp >>> console
  - 无

#### 更新客户端组

> user role
>
> - user

- UpdateClientGroup = 1702
- console >>> scmp
  - UpdateClientGroupRequest
- scmp >>> console
  - 无

### 客户端

#### 获取客户端

> user role
>
> - all

- GetClients = 1800
- console >>> scmp
  - GetClientsRequest
- scmp >>> console
  - GetClientsResponse

#### 迁移客户端

> user role
>
> - user

> client type
>
> - aix
> - file single
> - mscs
> - rac
> - rds
> - sing
> - vcenter

- MigrateClient = 1801
- console >>> scmp
  - MigrateClientRequest
- scmp >>> console
  - 无

#### 添加客户端

> user role
>
> - user

> client type
>
> - all

- AddClient = 1802
- console >>> scmp
  - AddClientRequest
- scmp >>> console
  - 无

#### 添加客户端（事务）

> 背景：客户端模块需要计数

> 使用场景：移交资源前目前用户没有 vCenter、云客户端时需要管理员添加

> user role
>
> - root
> - admin
>
> target user role
>
> - user

- AddClientExtX = 1803
- console >>> scmp
  - AddClientExtXRequest
- scmp >>> console
  - AddClientExtXResponse

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

#### 移除客户端（事务）

> user role
>
> - user

> client type
>
> - all

- RemoveClientX = 1804
- console >>> scmp
  - RemoveClientXRequest
- scmp >>> console
  - RemoveClientXResponse

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

#### 更新客户端 AKSK（事务）

> user role
>
> - user

> client type
>
> - ecs
> - oss
> - rds

- UpdateClientAkskX = 1805
- console >>> scmp
  - UpdateClientAkskXRequest
- scmp >>> console
  - UpdateClientAkskXResponse

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

### 客户端备份

#### 创建客户端备份

> user role
>
> - user

> client type
>
> - aix
> - ecs
> - oss
> - mdb

- CreateClientBackup = 1900
- console >>> scmp
  - CreateClientBackupRequest
- scmp >>> console
  - 无

#### 创建客户端备份（事务）

> user role
>
> - user

> client type
>
> - single
> - mscs
> - file single
> - vcenter
> - rds

- CreateClientBackupX = 1901
- console >>> scmp
  - CreateClientBackupXRequest
- scmp >>> console
  - CreateClientBackupXResponse

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

#### 删除客户端备份（事务）

> user role
>
> - user

> client type
>
> - single
> - mscs
> - aix
> - rac
> - vcenter
> - ecs
> - oss
> - rds
> - mdb

- DeleteClientBackupX = 1902
- console >>> scmp
  - DeleteClientBackupXRequest
- scmp >>> console
  - DeleteClientBackupXResponse

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

#### 更新客户端备份（事务）

> user role
>
> - user

> client type
>
> - single
> - mscs
> - aix
> - vcenter
> - rac

- UpdateClientBackupX = 1903
- console >>> scmp
  - UpdateClientBackupXRequest
- scmp >>> console
  - UpdateClientBackupXResponse

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

#### 创建容灾客户端备份（事务）

即创建容灾关系。

> user role
>
> - user

> client type
>
> - all

- CreateDrClientBackupX = 1904
- console >>> scmp
  - CreateDrClientBackupXRequest
- scmp >>> console
  - CreateDrClientBackupXResponse

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

#### 删除容灾客户端备份（事务）

即删除容灾关系。

> user role
>
> - user

> client type
>
> - all

- DeleteDrClientBackupX = 1905
- console >>> scmp
  - RemoveDisasterDiskBackupRequest
- scmp >>> console
  - RemoveDisasterDiskBackupResult

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

#### 更新容灾客户端备份（事务）

即更新容灾关系。

> user role
>
> - user

> client type
>
> - single
> - mscs
> - aix
> - file single
> - vcenter

- UpdateDrClientBackupX = 1906
- console >>> scmp
  - UpdateDrClientBackupXRequest
- scmp >>> console
  - UpdateDrClientBackupXResponse

> 支持事务，详见 [Console 提交事务](#Console 提交事务) 和 [Console 回滚事务](#Console 回滚事务)

### 概览

#### 获取概览信息

> user role
>
> - all

- GetOverview = 2000
- console >>> scmp
  - 无
- scmp >>> console
  - GetOverviewResponse

#### 获取概览数据方舟列表

> user role
>
> - all

- GetOverviewDataArks = 2001
- console >>> scmp
  - GetOverviewDataArksRequest
- scmp >>> console
  - GetOverviewDataArksResponse

#### 获取概览客户端列表

> user role
>
> - all

- GetOverviewClients = 2002
- console >>> scmp
  - GetOverviewClientsRequest
- scmp >>> console
  - GetOverviewClientsResponse

#### 获取概览 VMware 虚拟机列表

> user role
>
> - all

- GetOverviewVmwareVms = 2003
- console >>> scmp
  - GetOverviewVmwareVmsRequest
- scmp >>> console
  - GetOverviewVmwareVmsResponse

#### 获取概览云主机列表

> user role
>
> - all

- GetOverviewCloudHost = 2004
- console >>> scmp
  - GetOverviewCloudHostRequest
- scmp >>> console
  - GetOverviewCloudHostResponse

#### 获取用户报表

> user role
>
> - root
> - admin
> - auditor

- GetUserReport = 2005
- console >>> scmp
  - 无
- scmp >>> console
  - GetUserReportResponse

#### 获取数据方舟报表

> user role
>
> - root
> - admin
> - auditor

- GetDataArkReport = 2006
- console >>> scmp
  - 无
- scmp >>> console
  - GetDataArkReportResponse

#### 获取备份报表

> user role
>
> - root
> - admin
> - auditor

- GetBackupReport = 2007
- console >>> scmp
  - 无
- scmp >>> console
  - GetBackupReportResponse

### 报警日志

#### 获取报警日志

> user role
>
> - all

- GetAlarmLogs = 2100
- console >>> scmp
  - GetAlarmLogsRequest
- scmp >>> console
  - GetAlarmLogsResponse

#### 删除报警日志

> user role
>
> - all

- DeleteAlarmLog = 2101
- console >>> scmp
  - DeleteAlarmLogRequest
- scmp >>> console
  - 无

#### 设置报警日志处理状态

> user role
>
> - all

- UpdateAlarmLogProcessed = 2102
- console >>> scmp
  - UpdateAlarmLogProcessedRequest
- scmp >>> console
  - 无

### 操作日志

#### 获取操作日志

> user role
>
> - all

- GetOperationLogs = 2200
- console >>> scmp
  - GetOperationLogsRequest
- scmp >>> console
  - GetOperationLogsResponse

#### 插入操作日志

> user role
>
> - all

> 支持以下操作：
>
> - Command_ManageDataArk: `args [数据方舟 ID, 数据方舟名, 数据方舟 IP]`
> - Command_ExportDataArkLog: `args [null]`
> - Command_ExportDataArkReport: `args [null]`
> - Command_ExportUserReport: `args [null]`
> - Command_ExportBackupReport: `args [null]`
> - Command_CreateUserQuota: `args [目标用户ID, 目标用户显示名, 数据方舟 ID, 数据方舟名, 数据方舟 IP]`
> - Command_UpdateUserQuota: `args [目标用户ID, 目标用户显示名, 数据方舟 ID, 数据方舟名, 数据方舟 IP]`
> - Command_DeleteUserQuota: `args [目标用户ID, 目标用户显示名, 数据方舟 ID, 数据方舟名, 数据方舟 IP]`

- CreateOperationLog = 2201
- console >>> scmp
  - CreateOperationLogRequest
- scmp >>> console
  - 无

### 邮件报警

#### 获取邮件报警设置

> user role
>
> - all

- GetEmailAlarm = 2300
- console >>> scmp
  - 无
- scmp >>> console
  - GetEmailAlarmResponse

#### 更新邮件报警设置

> user role
>
> - all

- UpdateEmailAlarm = 2303
- console >>> scmp
  - UpdateEmailAlarmRequest
- scmp >>> console
  - 无

#### 验证邮件报警设置

> user role
>
> - all

- VerifyEmailAlarm = 2304
- console >>> scmp
  - VerifyEmailAlarmRequest
- scmp >>> console
  - 无

### SNMP

#### 获取 SNMP 配置

> user role
>
> - root
> - admin

- GetSnmp = 2400
- console >>> scmp
  - 无
- scmp >>> console
  - GetSnmpResponse

#### 更新 SNMP 配置

> user role
>
> - root
> - admin

- UpdateSnmp = 2401
- console >>> scmp
  - UpdateSnmpRequest
- scmp >>> console
  - 无

### 云服务端点

#### 获取云服务端点

> user role
>
> - root
> - admin

- GetCloudServiceEndpoint = 2500
- console >>> scmp
  - 无
- scmp >>> console
  - GetCloudServiceEndpointResponse

#### 更新云服务端点

> user role
>
> - root
> - admin

- UpdateCloudServiceEndpoint = 2501
- console >>> scmp
  - UpdateCloudServiceEndpointRequest
- scmp >>> console
  - 无

### 事务

#### 提交事务

> user role
>
> - all

- CommitTransaction = 2601
- console >>> scmp
  - CommitTransactionRequest
- scmp >>> console
  - 无

#### 回滚事务

> user role
>
> - all

- RollbackTransaction = 2602
- console >>> scmp
  - RollbackTransactionRequest
- scmp >>> console
  - 无

### 登出

#### 通知 Console 被登出

- LogoutCloudManager = 1002
- scmp >>> console
  - 无
  - 错误信息参见消息头

## scmp 和 mailalarm 的操作

### 数据方舟

#### 添加数据方舟

- AddDataArk = 1501
- scmp >>> mailalarm
  - AddDataArkRequest
- mailalarm >>> scmp
  - 无

#### 移除数据方舟

- RemoveDataArk = 1502
- scmp >>> mailalarm
  - RemoveDataArkRequest
- mailalarm >>> scmp
  - 无

#### 更新数据方舟信息

- UpdateDataArk = 1504
- scmp >>> mailalarm
  - UpdateDataArkRequest
- mailalarm >>> scmp
  - 无

### 邮件报警

#### 创建邮件报警设置

- CreateEmailAlarm = 2301
- scmp >>> mailalarm
  - CreateEmailAlarmRequest
- mailalarm >>> scmp
  - 无

#### 删除邮件报警设置

- DeleteEmailAlarm = 2302
- scmp >>> mailalarm
  - DeleteEmailAlarmRequest
- mailalarm >>> scmp
  - 无

#### 更新邮件报警设置

- UpdateEmailAlarm = 2303
- scmp >>> mailalarm
  - UpdateEmailAlarmRequest
- mailalarm >>> scmp
  - 无

#### 验证邮件报警设置

- VerifyEmailAlarm = 2304
- scmp >>> mailalarm
  - VerifyEmailAlarmRequest
- mailalarm >>> scmp
  - 无

## scmp 和 streamer 的操作

### 配额

#### 查询备份空间是否足够

场景：

- 云客户端的备份关系创建备份点时

协议：

- QueryBackupSpaceEnough = 1606
- streamer >>> scmp
  - QueryBackupSpaceEnoughRequest
- scmp >>> streamer
  - QueryBackupSpaceEnoughResponse
