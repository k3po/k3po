# Change Log

## [Unreleased](https://github.com/k3po/k3po/tree/HEAD)

[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-66...HEAD)

**Merged pull requests:**

- Implement aborted event [\#425](https://github.com/k3po/k3po/pull/425) ([jfallows](https://github.com/jfallows))
- Updated license to apache [\#423](https://github.com/k3po/k3po/pull/423) ([dpwspoon](https://github.com/dpwspoon))
- Fix typo in return type [\#422](https://github.com/k3po/k3po/pull/422) ([jfallows](https://github.com/jfallows))
- Removal of DISPOSE and DISPOSED messages. [\#417](https://github.com/k3po/k3po/pull/417) ([StCostea](https://github.com/StCostea))

## [3.0.0-alpha-66](https://github.com/k3po/k3po/tree/3.0.0-alpha-66) (2017-04-05)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-65...3.0.0-alpha-66)

**Implemented enhancements:**

- Extensible grammar and runtime [\#420](https://github.com/k3po/k3po/pull/420) ([jfallows](https://github.com/jfallows))

## [3.0.0-alpha-65](https://github.com/k3po/k3po/tree/3.0.0-alpha-65) (2017-03-21)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-64...3.0.0-alpha-65)

**Fixed bugs:**

- Thread leaks in K3po driver [\#405](https://github.com/k3po/k3po/issues/405)
- Unexpected event Notified [\#402](https://github.com/k3po/k3po/issues/402)
- Test in http specification - ConnectionManagementIT - fails in specific conditions [\#395](https://github.com/k3po/k3po/issues/395)
- Interrupted status true at the beginning of test [\#391](https://github.com/k3po/k3po/issues/391)

**Closed issues:**

- Rename `Finished` header names in control protocol [\#413](https://github.com/k3po/k3po/issues/413)

**Merged pull requests:**

- Send/Receive each data channel message on one line [\#418](https://github.com/k3po/k3po/pull/418) ([Anisotrop](https://github.com/Anisotrop))
- Rename `Finished` header names in control protocol [\#416](https://github.com/k3po/k3po/pull/416) ([StCostea](https://github.com/StCostea))
- Fix.turn.spec.channel.bind [\#415](https://github.com/k3po/k3po/pull/415) ([Anisotrop](https://github.com/Anisotrop))
- Sporadic failure in ConnectionManagementIT [\#410](https://github.com/k3po/k3po/pull/410) ([StCostea](https://github.com/StCostea))
- Thread leaks in K3po [\#407](https://github.com/k3po/k3po/pull/407) ([StCostea](https://github.com/StCostea))

## [3.0.0-alpha-64](https://github.com/k3po/k3po/tree/3.0.0-alpha-64) (2017-03-08)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-63...3.0.0-alpha-64)

**Fixed bugs:**

- Incorrect implementation for ControlDecoder without using the ReplayingDecoder framework [\#393](https://github.com/k3po/k3po/issues/393)
- K3po driver is stuck if an abort is sent after an await on a barrier never triggered [\#388](https://github.com/k3po/k3po/issues/388)

**Closed issues:**

- Regresssion:  k3po-maven-plugin stop is taking ages, over 1 minute [\#400](https://github.com/k3po/k3po/issues/400)

**Merged pull requests:**

- Additional fixes related to interrupted status and 'Unexpected event Notified' [\#406](https://github.com/k3po/k3po/pull/406) ([StCostea](https://github.com/StCostea))
- Finish execution before sending the DISPOSE command [\#404](https://github.com/k3po/k3po/pull/404) ([StCostea](https://github.com/StCostea))

## [3.0.0-alpha-63](https://github.com/k3po/k3po/tree/3.0.0-alpha-63) (2017-02-14)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-62...3.0.0-alpha-63)

**Merged pull requests:**

- Workaround \#147 in OptionsResolver too [\#403](https://github.com/k3po/k3po/pull/403) ([jfallows](https://github.com/jfallows))

## [3.0.0-alpha-62](https://github.com/k3po/k3po/tree/3.0.0-alpha-62) (2017-02-09)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-61...3.0.0-alpha-62)

**Merged pull requests:**

- Fix clean-up issue in ControlServerHandler [\#401](https://github.com/k3po/k3po/pull/401) ([StCostea](https://github.com/StCostea))

## [3.0.0-alpha-61](https://github.com/k3po/k3po/tree/3.0.0-alpha-61) (2017-02-07)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-60...3.0.0-alpha-61)

**Closed issues:**

- Agrona transport is flushing partial write when unexpected data is read [\#397](https://github.com/k3po/k3po/issues/397)

**Merged pull requests:**

- Agrona transport is flushing partial write when unexpected data is read [\#399](https://github.com/k3po/k3po/pull/399) ([jfallows](https://github.com/jfallows))
- removed system out println for error message [\#396](https://github.com/k3po/k3po/pull/396) ([dpwspoon](https://github.com/dpwspoon))
- \[Merge after PR 383 and PR 389\] Control protocol testcases [\#390](https://github.com/k3po/k3po/pull/390) ([StCostea](https://github.com/StCostea))
- K3po driver fixes according to new specs + some additional fixes [\#389](https://github.com/k3po/k3po/pull/389) ([StCostea](https://github.com/StCostea))
- Control protocol specs [\#383](https://github.com/k3po/k3po/pull/383) ([StCostea](https://github.com/StCostea))

## [3.0.0-alpha-60](https://github.com/k3po/k3po/tree/3.0.0-alpha-60) (2016-12-20)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-59...3.0.0-alpha-60)

## [3.0.0-alpha-59](https://github.com/k3po/k3po/tree/3.0.0-alpha-59) (2016-12-19)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-58...3.0.0-alpha-59)

**Merged pull requests:**

- Added spec tests for redirection on http [\#386](https://github.com/k3po/k3po/pull/386) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-58](https://github.com/k3po/k3po/tree/3.0.0-alpha-58) (2016-12-13)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-57...3.0.0-alpha-58)

## [3.0.0-alpha-57](https://github.com/k3po/k3po/tree/3.0.0-alpha-57) (2016-12-13)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-56...3.0.0-alpha-57)

**Merged pull requests:**

- Adding a test case where udp writes are done together [\#382](https://github.com/k3po/k3po/pull/382) ([jitsni](https://github.com/jitsni))

## [3.0.0-alpha-56](https://github.com/k3po/k3po/tree/3.0.0-alpha-56) (2016-12-06)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-55...3.0.0-alpha-56)

**Merged pull requests:**

- HTTP Host header shouldn't have scheme [\#381](https://github.com/k3po/k3po/pull/381) ([jitsni](https://github.com/jitsni))

## [3.0.0-alpha-55](https://github.com/k3po/k3po/tree/3.0.0-alpha-55) (2016-11-11)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-54...3.0.0-alpha-55)

**Merged pull requests:**

-  case-insensitive websocket header names [\#380](https://github.com/k3po/k3po/pull/380) ([jitsni](https://github.com/jitsni))

## [3.0.0-alpha-54](https://github.com/k3po/k3po/tree/3.0.0-alpha-54) (2016-11-03)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-53...3.0.0-alpha-54)

**Merged pull requests:**

- Implement k3po/k3po\#377 [\#378](https://github.com/k3po/k3po/pull/378) ([jfallows](https://github.com/jfallows))

## [3.0.0-alpha-53](https://github.com/k3po/k3po/tree/3.0.0-alpha-53) (2016-10-18)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-52...3.0.0-alpha-53)

**Merged pull requests:**

- Defer upstreams until downstream is completed… [\#375](https://github.com/k3po/k3po/pull/375) ([jfallows](https://github.com/jfallows))

## [3.0.0-alpha-52](https://github.com/k3po/k3po/tree/3.0.0-alpha-52) (2016-10-17)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-51...3.0.0-alpha-52)

**Merged pull requests:**

- Turn.proxy.tcp.allocations [\#374](https://github.com/k3po/k3po/pull/374) ([Anisotrop](https://github.com/Anisotrop))
- Http.multi.auth [\#372](https://github.com/k3po/k3po/pull/372) ([dpwspoon](https://github.com/dpwspoon))
- Fixed errors in javadoc [\#364](https://github.com/k3po/k3po/pull/364) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-51](https://github.com/k3po/k3po/tree/3.0.0-alpha-51) (2016-10-03)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-50...3.0.0-alpha-51)

**Merged pull requests:**

- Race between downstream and upstream connects  [\#371](https://github.com/k3po/k3po/pull/371) ([jitsni](https://github.com/jitsni))

## [3.0.0-alpha-50](https://github.com/k3po/k3po/tree/3.0.0-alpha-50) (2016-10-02)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-49...3.0.0-alpha-50)

**Merged pull requests:**

- There is race between downstream and upstream connects : [\#370](https://github.com/k3po/k3po/pull/370) ([jitsni](https://github.com/jitsni))

## [3.0.0-alpha-49](https://github.com/k3po/k3po/tree/3.0.0-alpha-49) (2016-10-01)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-48...3.0.0-alpha-49)

**Merged pull requests:**

- Wse Upstream and downstream connects are racy [\#369](https://github.com/k3po/k3po/pull/369) ([jitsni](https://github.com/jitsni))

## [3.0.0-alpha-48](https://github.com/k3po/k3po/tree/3.0.0-alpha-48) (2016-09-27)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-47...3.0.0-alpha-48)

**Merged pull requests:**

- Test message digest [\#367](https://github.com/k3po/k3po/pull/367) ([NicoletaOita](https://github.com/NicoletaOita))
- Moved scripts for MaskingIPv6IT from k3po to gateway [\#366](https://github.com/k3po/k3po/pull/366) ([mgherghe](https://github.com/mgherghe))

## [3.0.0-alpha-47](https://github.com/k3po/k3po/tree/3.0.0-alpha-47) (2016-09-19)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-46...3.0.0-alpha-47)

**Merged pull requests:**

- Add support for IPv4 embedded address into IPv6 address. [\#365](https://github.com/k3po/k3po/pull/365) ([Anisotrop](https://github.com/Anisotrop))

## [3.0.0-alpha-46](https://github.com/k3po/k3po/tree/3.0.0-alpha-46) (2016-09-15)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-45...3.0.0-alpha-46)

**Merged pull requests:**

- Fix for shouldGive400WithIncorrectLength test with UDP [\#363](https://github.com/k3po/k3po/pull/363) ([mgherghe](https://github.com/mgherghe))
- Turn tests with channel data [\#362](https://github.com/k3po/k3po/pull/362) ([NicoletaOita](https://github.com/NicoletaOita))

## [3.0.0-alpha-45](https://github.com/k3po/k3po/tree/3.0.0-alpha-45) (2016-09-08)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-44...3.0.0-alpha-45)

**Merged pull requests:**

- update ws extensions scripts to use syntax for multivalued headers [\#349](https://github.com/k3po/k3po/pull/349) ([danibusu](https://github.com/danibusu))

## [3.0.0-alpha-44](https://github.com/k3po/k3po/tree/3.0.0-alpha-44) (2016-09-06)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-43...3.0.0-alpha-44)

**Merged pull requests:**

- Turn tests for mapped address and masking key with ipv6. [\#359](https://github.com/k3po/k3po/pull/359) ([NicoletaOita](https://github.com/NicoletaOita))

## [3.0.0-alpha-43](https://github.com/k3po/k3po/tree/3.0.0-alpha-43) (2016-09-02)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-42...3.0.0-alpha-43)

## [3.0.0-alpha-42](https://github.com/k3po/k3po/tree/3.0.0-alpha-42) (2016-09-02)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-41...3.0.0-alpha-42)

**Merged pull requests:**

- Added test for TURN Data Message [\#361](https://github.com/k3po/k3po/pull/361) ([dpwspoon](https://github.com/dpwspoon))
- Upgrade to use maven compat … [\#360](https://github.com/k3po/k3po/pull/360) ([jfallows](https://github.com/jfallows))
- Added function for IPv6 support in turn.proxy. [\#357](https://github.com/k3po/k3po/pull/357) ([Anisotrop](https://github.com/Anisotrop))
- Added WSE test that there are no extensions negotiated [\#356](https://github.com/k3po/k3po/pull/356) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-41](https://github.com/k3po/k3po/tree/3.0.0-alpha-41) (2016-08-18)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-40...3.0.0-alpha-41)

**Merged pull requests:**

- Fix message sizes [\#355](https://github.com/k3po/k3po/pull/355) ([Anisotrop](https://github.com/Anisotrop))

## [3.0.0-alpha-40](https://github.com/k3po/k3po/tree/3.0.0-alpha-40) (2016-08-17)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-39...3.0.0-alpha-40)

**Fixed bugs:**

- specification/http build failing sporadically in some tests with expected read closed got disconnected [\#251](https://github.com/k3po/k3po/issues/251)

**Merged pull requests:**

- K3po.turn.spec updates [\#354](https://github.com/k3po/k3po/pull/354) ([Anisotrop](https://github.com/Anisotrop))
- fix httpx k3po scripts masking [\#352](https://github.com/k3po/k3po/pull/352) ([danibusu](https://github.com/danibusu))

## [3.0.0-alpha-39](https://github.com/k3po/k3po/tree/3.0.0-alpha-39) (2016-08-10)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-38...3.0.0-alpha-39)

**Merged pull requests:**

- RFC-7231 and RFC-7233 Spec. Tests [\#343](https://github.com/k3po/k3po/pull/343) ([a-zuckut](https://github.com/a-zuckut))

## [3.0.0-alpha-38](https://github.com/k3po/k3po/tree/3.0.0-alpha-38) (2016-07-29)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-37...3.0.0-alpha-38)

**Merged pull requests:**

- K3po.turn.spec [\#350](https://github.com/k3po/k3po/pull/350) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-37](https://github.com/k3po/k3po/tree/3.0.0-alpha-37) (2016-07-25)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-36...3.0.0-alpha-37)

**Merged pull requests:**

- Wrong header parameter "Host:" [\#348](https://github.com/k3po/k3po/pull/348) ([DoruM](https://github.com/DoruM))

## [3.0.0-alpha-36](https://github.com/k3po/k3po/tree/3.0.0-alpha-36) (2016-07-22)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-35...3.0.0-alpha-36)

**Merged pull requests:**

- Upgrade to Agrona 0.5.2, consuming Agrona package name change [\#347](https://github.com/k3po/k3po/pull/347) ([jfallows](https://github.com/jfallows))

## [3.0.0-alpha-35](https://github.com/k3po/k3po/tree/3.0.0-alpha-35) (2016-07-14)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-34...3.0.0-alpha-35)

**Closed issues:**

- K3po Abort command races backlogged messages. [\#332](https://github.com/k3po/k3po/issues/332)

**Merged pull requests:**

- Some more changes for udp specification scripts. [\#345](https://github.com/k3po/k3po/pull/345) ([jitsni](https://github.com/jitsni))
- Adding spec tests for UDP [\#344](https://github.com/k3po/k3po/pull/344) ([jitsni](https://github.com/jitsni))
- UDP support [\#341](https://github.com/k3po/k3po/pull/341) ([jitsni](https://github.com/jitsni))
- Upgrading to netty 3.10.5 [\#340](https://github.com/k3po/k3po/pull/340) ([jitsni](https://github.com/jitsni))

## [3.0.0-alpha-34](https://github.com/k3po/k3po/tree/3.0.0-alpha-34) (2016-04-20)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-33...3.0.0-alpha-34)

**Merged pull requests:**

- Removed checking of event to early as this is done with implicit barr… [\#336](https://github.com/k3po/k3po/pull/336) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-33](https://github.com/k3po/k3po/tree/3.0.0-alpha-33) (2016-04-19)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-32...3.0.0-alpha-33)

**Merged pull requests:**

- Reverted fix as it was incorrect, events that arrive too early should fail [\#335](https://github.com/k3po/k3po/pull/335) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-32](https://github.com/k3po/k3po/tree/3.0.0-alpha-32) (2016-04-19)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-31...3.0.0-alpha-32)

**Merged pull requests:**

- Fixed if else statements to be inclusive [\#334](https://github.com/k3po/k3po/pull/334) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-31](https://github.com/k3po/k3po/tree/3.0.0-alpha-31) (2016-04-19)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-30...3.0.0-alpha-31)

**Merged pull requests:**

- Fix where barrierFuture should be listened to even if handler future … [\#333](https://github.com/k3po/k3po/pull/333) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-30](https://github.com/k3po/k3po/tree/3.0.0-alpha-30) (2016-04-15)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-29...3.0.0-alpha-30)

**Closed issues:**

- Sporadic NPE in Control.java on Ubuntu 14.04 [\#330](https://github.com/k3po/k3po/issues/330)

**Merged pull requests:**

- Moved controller.dispose into proper try catch block [\#331](https://github.com/k3po/k3po/pull/331) ([dpwspoon](https://github.com/dpwspoon))
- fixed regex issue [\#329](https://github.com/k3po/k3po/pull/329) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-29](https://github.com/k3po/k3po/tree/3.0.0-alpha-29) (2016-04-13)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-28...3.0.0-alpha-29)

**Merged pull requests:**

- Removed all instance where fragmentation in regex could cause a test … [\#328](https://github.com/k3po/k3po/pull/328) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-28](https://github.com/k3po/k3po/tree/3.0.0-alpha-28) (2016-04-07)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-27...3.0.0-alpha-28)

**Merged pull requests:**

- Caught exceptions that cause catostrophic failures in K3po and thus c… [\#325](https://github.com/k3po/k3po/pull/325) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-27](https://github.com/k3po/k3po/tree/3.0.0-alpha-27) (2016-04-02)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-26...3.0.0-alpha-27)

**Merged pull requests:**

- Http only identity for amqp identity promotion [\#324](https://github.com/k3po/k3po/pull/324) ([ilyaanisimov-kaazing](https://github.com/ilyaanisimov-kaazing))

## [3.0.0-alpha-26](https://github.com/k3po/k3po/tree/3.0.0-alpha-26) (2016-03-31)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-25...3.0.0-alpha-26)

**Merged pull requests:**

- Sec-WebSocket-Protocol header contains comma separated value list. [\#322](https://github.com/k3po/k3po/pull/322) ([jitsni](https://github.com/jitsni))

## [3.0.0-alpha-25](https://github.com/k3po/k3po/tree/3.0.0-alpha-25) (2016-03-29)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-24...3.0.0-alpha-25)

**Merged pull requests:**

- amqp tests scripts fixed [\#321](https://github.com/k3po/k3po/pull/321) ([ilyaanisimov-kaazing](https://github.com/ilyaanisimov-kaazing))

## [3.0.0-alpha-24](https://github.com/k3po/k3po/tree/3.0.0-alpha-24) (2016-03-16)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-23...3.0.0-alpha-24)

**Merged pull requests:**

- moved scripts from gateway transport http to k3po [\#318](https://github.com/k3po/k3po/pull/318) ([ilyaanisimov-kaazing](https://github.com/ilyaanisimov-kaazing))
- Added tests for WS compliance \(maximum message size and maximum lifetime\) [\#316](https://github.com/k3po/k3po/pull/316) ([NicoletaOita](https://github.com/NicoletaOita))
- Added support for http chunking trailers, \(and worked on adding extensions\),  [\#314](https://github.com/k3po/k3po/pull/314) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-23](https://github.com/k3po/k3po/tree/3.0.0-alpha-23) (2016-03-08)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-22...3.0.0-alpha-23)

**Merged pull requests:**

- http method testing for GET,POST,OUT,HEAD [\#315](https://github.com/k3po/k3po/pull/315) ([ilyaanisimov-kaazing](https://github.com/ilyaanisimov-kaazing))

## [3.0.0-alpha-22](https://github.com/k3po/k3po/tree/3.0.0-alpha-22) (2016-03-03)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-21...3.0.0-alpha-22)

**Merged pull requests:**

- Adding some more negative tests for httpxe [\#312](https://github.com/k3po/k3po/pull/312) ([jitsni](https://github.com/jitsni))
- Adding couple of negative tests for origin security [\#311](https://github.com/k3po/k3po/pull/311) ([jitsni](https://github.com/jitsni))

## [3.0.0-alpha-21](https://github.com/k3po/k3po/tree/3.0.0-alpha-21) (2016-02-26)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-20...3.0.0-alpha-21)

**Merged pull requests:**

- Parameterized scripts to use location property in accept and connects [\#310](https://github.com/k3po/k3po/pull/310) ([dpwspoon](https://github.com/dpwspoon))
- Add ability to override property from control [\#309](https://github.com/k3po/k3po/pull/309) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-20](https://github.com/k3po/k3po/tree/3.0.0-alpha-20) (2016-02-19)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-19...3.0.0-alpha-20)

**Merged pull requests:**

- Fixed wseb spec test script client.abruptly.closes.upstream/request  [\#308](https://github.com/k3po/k3po/pull/308) ([cmebarrow](https://github.com/cmebarrow))

## [3.0.0-alpha-19](https://github.com/k3po/k3po/tree/3.0.0-alpha-19) (2016-02-16)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-18...3.0.0-alpha-19)

**Merged pull requests:**

- More wse spec test fixes [\#307](https://github.com/k3po/k3po/pull/307) ([cmebarrow](https://github.com/cmebarrow))

## [3.0.0-alpha-18](https://github.com/k3po/k3po/tree/3.0.0-alpha-18) (2016-02-12)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-17...3.0.0-alpha-18)

**Implemented enhancements:**

- Specification.k3po.control - Enhance control protocol to support overriding default property values [\#154](https://github.com/k3po/k3po/issues/154)

**Merged pull requests:**

- Wse spec test fixes [\#305](https://github.com/k3po/k3po/pull/305) ([cmebarrow](https://github.com/cmebarrow))

## [3.0.0-alpha-17](https://github.com/k3po/k3po/tree/3.0.0-alpha-17) (2016-02-04)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-16...3.0.0-alpha-17)

**Implemented enhancements:**

- Http Sematics to support chunked encoding extensions and trailer [\#136](https://github.com/k3po/k3po/issues/136)

**Merged pull requests:**

- Reverted the port numbers and the paths for extended handshake script… [\#304](https://github.com/k3po/k3po/pull/304) ([sanjay-saxena](https://github.com/sanjay-saxena))

## [3.0.0-alpha-16](https://github.com/k3po/k3po/tree/3.0.0-alpha-16) (2016-02-03)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-15...3.0.0-alpha-16)

## [3.0.0-alpha-15](https://github.com/k3po/k3po/tree/3.0.0-alpha-15) (2016-02-03)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-14...3.0.0-alpha-15)

**Fixed bugs:**

- BehaviorIT.testDelayedHttpClientConnect Specified behavior sporadic failures [\#194](https://github.com/k3po/k3po/issues/194)

**Closed issues:**

- Add ability to load an origin page on demand [\#272](https://github.com/k3po/k3po/issues/272)

**Merged pull requests:**

- Adds abort and aborted command [\#303](https://github.com/k3po/k3po/pull/303) ([dpwspoon](https://github.com/dpwspoon))
- \(\#298\) Changed WSE SPEC.md to allow clients to include a request body… [\#300](https://github.com/k3po/k3po/pull/300) ([cmebarrow](https://github.com/cmebarrow))

## [3.0.0-alpha-14](https://github.com/k3po/k3po/tree/3.0.0-alpha-14) (2016-02-03)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-13...3.0.0-alpha-14)

**Implemented enhancements:**

- Ability for http scripts to force a tcp close: abort command [\#132](https://github.com/k3po/k3po/issues/132)
- Provide HTTP-level examples that demonstrate ground-up Robot Capabilities [\#11](https://github.com/k3po/k3po/issues/11)
- Provide TCP-level examples that demonstrate ground-up Robot Capabilities [\#10](https://github.com/k3po/k3po/issues/10)

**Closed issues:**

- HTTPX: x-kaazing-handshake protocol - Empty WebSocket binary frame marks the end of extended handshake response [\#299](https://github.com/k3po/k3po/issues/299)

**Merged pull requests:**

- Updated the port numbers and the paths in the scripts to match the ones used in client itests [\#302](https://github.com/k3po/k3po/pull/302) ([sanjay-saxena](https://github.com/sanjay-saxena))

## [3.0.0-alpha-13](https://github.com/k3po/k3po/tree/3.0.0-alpha-13) (2016-01-27)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-12...3.0.0-alpha-13)

**Merged pull requests:**

- Added ws spec test LimitsIT plus scripts to test for ws frames exceed… [\#296](https://github.com/k3po/k3po/pull/296) ([cmebarrow](https://github.com/cmebarrow))
- Httpxe specification tests [\#293](https://github.com/k3po/k3po/pull/293) ([jitsni](https://github.com/jitsni))
- SSE spec tests [\#291](https://github.com/k3po/k3po/pull/291) ([sanjay-saxena](https://github.com/sanjay-saxena))

## [3.0.0-alpha-12](https://github.com/k3po/k3po/tree/3.0.0-alpha-12) (2016-01-19)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-11...3.0.0-alpha-12)

**Merged pull requests:**

- Update userinfo to precede hostname and port in rfc7230 scripts [\#294](https://github.com/k3po/k3po/pull/294) ([jfallows](https://github.com/jfallows))

## [3.0.0-alpha-11](https://github.com/k3po/k3po/tree/3.0.0-alpha-11) (2016-01-15)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-9...3.0.0-alpha-11)

**Closed issues:**

- httpx specification and tests should require the server to close the websocket connection after issuing a redirect \(302\) [\#283](https://github.com/k3po/k3po/issues/283)
- WSE specification does not specify the behavior for invalid upstream command frames [\#282](https://github.com/k3po/k3po/issues/282)

**Merged pull requests:**

- Wse: improve spec and tests [\#290](https://github.com/k3po/k3po/pull/290) ([cmebarrow](https://github.com/cmebarrow))
- Wse binary as escaped text: spec and test fixes [\#289](https://github.com/k3po/k3po/pull/289) ([cmebarrow](https://github.com/cmebarrow))
- Update comunity dependency from 2.15 to 2.18, add license to two wse … [\#288](https://github.com/k3po/k3po/pull/288) ([cmebarrow](https://github.com/cmebarrow))
- Issue 282 wse invalid upstream body [\#287](https://github.com/k3po/k3po/pull/287) ([cmebarrow](https://github.com/cmebarrow))
- Update community and license [\#286](https://github.com/k3po/k3po/pull/286) ([cmebarrow](https://github.com/cmebarrow))
- Issue \#283: HTTPX specification and test changes to mandate that server [\#285](https://github.com/k3po/k3po/pull/285) ([cmebarrow](https://github.com/cmebarrow))
- Added interpretation to wse spec on correct behavior.  [\#280](https://github.com/k3po/k3po/pull/280) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-9](https://github.com/k3po/k3po/tree/3.0.0-alpha-9) (2016-01-08)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-8...3.0.0-alpha-9)

**Merged pull requests:**

- Make sure BBOSH FINISH is sent only after sending all NOTIFIED messages  [\#284](https://github.com/k3po/k3po/pull/284) ([cmebarrow](https://github.com/cmebarrow))
- RFC 7234 spec tests [\#270](https://github.com/k3po/k3po/pull/270) ([sanjay-saxena](https://github.com/sanjay-saxena))

## [3.0.0-alpha-8](https://github.com/k3po/k3po/tree/3.0.0-alpha-8) (2015-12-30)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-7...3.0.0-alpha-8)

**Merged pull requests:**

- Added wse escaped text test to echo non escaped bytes [\#281](https://github.com/k3po/k3po/pull/281) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-7](https://github.com/k3po/k3po/tree/3.0.0-alpha-7) (2015-12-23)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-6...3.0.0-alpha-7)

**Merged pull requests:**

- Test for wseb escaped text [\#276](https://github.com/k3po/k3po/pull/276) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-6](https://github.com/k3po/k3po/tree/3.0.0-alpha-6) (2015-12-22)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-5...3.0.0-alpha-6)

**Implemented enhancements:**

- Enhance script syntax to support overridable properties with defaults [\#52](https://github.com/k3po/k3po/issues/52)

**Closed issues:**

- Package name for Functions class in specification.httpx project conflicts with specification.http [\#277](https://github.com/k3po/k3po/issues/277)

**Merged pull requests:**

- Change package name of Functions class in specification.httpx to avoi… [\#278](https://github.com/k3po/k3po/pull/278) ([cmebarrow](https://github.com/cmebarrow))
- Resolve transport option expressions in scripts [\#275](https://github.com/k3po/k3po/pull/275) ([jfallows](https://github.com/jfallows))
- dispose command timeout is now infinite on debug [\#268](https://github.com/k3po/k3po/pull/268) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-5](https://github.com/k3po/k3po/tree/3.0.0-alpha-5) (2015-12-02)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-4...3.0.0-alpha-5)

**Merged pull requests:**

- Reduce frequency of Agrona boss and worker run loops [\#274](https://github.com/k3po/k3po/pull/274) ([jfallows](https://github.com/jfallows))
- Added origin script for k3po.js [\#273](https://github.com/k3po/k3po/pull/273) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-4](https://github.com/k3po/k3po/tree/3.0.0-alpha-4) (2015-11-24)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-3...3.0.0-alpha-4)

**Implemented enhancements:**

- Add new command to k3po to execute clean up from junit rule. [\#239](https://github.com/k3po/k3po/issues/239)
- Specification.socks5 - Connect with GSSAPI authentication [\#180](https://github.com/k3po/k3po/issues/180)
- Specification.Socks - Connect with username/password authetication [\#179](https://github.com/k3po/k3po/issues/179)
- Specification.Socks - Establish remote BIND with SOCKS5 server [\#178](https://github.com/k3po/k3po/issues/178)
- Specification.Socks - Establish CONNECT on behalf of client [\#177](https://github.com/k3po/k3po/issues/177)
- Specification.Socks - UDP Associate tests [\#176](https://github.com/k3po/k3po/issues/176)
- Specification.Http2 - Negotiate HTT2 over TLS [\#174](https://github.com/k3po/k3po/issues/174)
- Specification.Http2 - Plain text \(i.e. "h2c"\) connection negotiation negative tests [\#173](https://github.com/k3po/k3po/issues/173)
- Specification.Http2 - HTTP headers tests -- static table for compression context [\#172](https://github.com/k3po/k3po/issues/172)
- Specification.Http2 - DATA frames [\#171](https://github.com/k3po/k3po/issues/171)
- Specification.Http2 - PING frame [\#170](https://github.com/k3po/k3po/issues/170)
- Specification.Http2 - Flow control tests \(WINDOW\_UPDATE\) [\#169](https://github.com/k3po/k3po/issues/169)
- Specification.Http2 - PRIORITY frame tests [\#168](https://github.com/k3po/k3po/issues/168)
- Specification.Http2 - Proxy style tests [\#167](https://github.com/k3po/k3po/issues/167)
- Specification.Http2 - HTTP 2.0 extensions [\#166](https://github.com/k3po/k3po/issues/166)
- Specification.Http2 - HEADERS compression: full static table coverage [\#165](https://github.com/k3po/k3po/issues/165)
- Specification.Http2 - HEADERS compression: dynamic table [\#164](https://github.com/k3po/k3po/issues/164)
- Specification.Http - RFC-7230: Message Routing [\#163](https://github.com/k3po/k3po/issues/163)
- Specification.Http - RFC-7231 Request Header Fields [\#162](https://github.com/k3po/k3po/issues/162)
- Specification.Http - RFC-7231 Request Methods [\#161](https://github.com/k3po/k3po/issues/161)
- Specification.Http - RFC-7231 Response Status Codes [\#160](https://github.com/k3po/k3po/issues/160)
- Specification.Http - RFC-7235 Authorization Tests [\#159](https://github.com/k3po/k3po/issues/159)
- Specification.TLS - Adding rough Client Hello, Server Hello script [\#158](https://github.com/k3po/k3po/issues/158)
- Specification.WSE - Spec tests for binary as text, binary as escaped text and binary as mixed text encodings [\#157](https://github.com/k3po/k3po/issues/157)
- Specification.WSE - Capture upstream/downstream url via regular expression and use it for upstream/downstream connect [\#156](https://github.com/k3po/k3po/issues/156)
- Specification.WSE - Can the X-WebSocket-Extensions header appear multiple times in create response? [\#155](https://github.com/k3po/k3po/issues/155)
- Specification.WS - Generate random Sec-WebSocket-Key [\#153](https://github.com/k3po/k3po/issues/153)
- Specifcation.WS - Need wss tests [\#152](https://github.com/k3po/k3po/issues/152)

**Fixed bugs:**

- Using an "await barrier" at the end of a script causes the test to hang [\#237](https://github.com/k3po/k3po/issues/237)
- httpxe specification tests notably in RequestsIT and OriginSecurityIT sometimes fail due to failed connect stream [\#229](https://github.com/k3po/k3po/issues/229)
- MessageFormatIT\#inboundShouldAcceptHeaders sporadically failing [\#187](https://github.com/k3po/k3po/issues/187)
- Specification.Socks - fix how docker-kdc is used in the tests [\#175](https://github.com/k3po/k3po/issues/175)

**Closed issues:**

- specification/bbosh build failed in test BBoshSpecificationIT, probably out of order connects [\#262](https://github.com/k3po/k3po/issues/262)
- http specification tests ConnectionManagementIT, TransferCodingsIT failing with read... instead of closed [\#260](https://github.com/k3po/k3po/issues/260)
- wse specification test DownstreamIT sometimes fails because HTTP requests are executed out of order [\#259](https://github.com/k3po/k3po/issues/259)

**Merged pull requests:**

- Updated annotations of StopMojo, which with out was causing a error l… [\#271](https://github.com/k3po/k3po/pull/271) ([dpwspoon](https://github.com/dpwspoon))
- Modified the request.rpt scripts to read the control bytes from the initial handshake and not hardcode them. [\#269](https://github.com/k3po/k3po/pull/269) ([NicoletaOita](https://github.com/NicoletaOita))
- Simplify debug output... [\#266](https://github.com/k3po/k3po/pull/266) ([jfallows](https://github.com/jfallows))
- Log the "FAILED during remove" message in ExecutionHandler at debug level [\#265](https://github.com/k3po/k3po/pull/265) ([cmebarrow](https://github.com/cmebarrow))
- Script corrections to reduce build failures, especially on Travis, plus some diagnostic improvements [\#263](https://github.com/k3po/k3po/pull/263) ([cmebarrow](https://github.com/cmebarrow))
- Added Dispose/Disposed to command and event protocol to clean up k3po driver [\#241](https://github.com/k3po/k3po/pull/241) ([dpwspoon](https://github.com/dpwspoon))

## [3.0.0-alpha-3](https://github.com/k3po/k3po/tree/3.0.0-alpha-3) (2015-11-09)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-2...3.0.0-alpha-3)

**Closed issues:**

- Using 'connect await BARRIER' instead of `write await BARRIER` causes ScriptParseException [\#257](https://github.com/k3po/k3po/issues/257)

**Merged pull requests:**

- Add test for accept notify barrier [\#255](https://github.com/k3po/k3po/pull/255) ([jfallows](https://github.com/jfallows))
- Support accept notify to be used in conjuction with connect await [\#254](https://github.com/k3po/k3po/pull/254) ([jfallows](https://github.com/jfallows))
- Issue \#571: Added RFC 7232 specification tests. [\#247](https://github.com/k3po/k3po/pull/247) ([sanjay-saxena](https://github.com/sanjay-saxena))

## [3.0.0-alpha-2](https://github.com/k3po/k3po/tree/3.0.0-alpha-2) (2015-11-04)
[Full Changelog](https://github.com/k3po/k3po/compare/3.0.0-alpha-1...3.0.0-alpha-2)

**Fixed bugs:**

- Can't get client hard close to work with http after websocket upgrade [\#223](https://github.com/k3po/k3po/issues/223)

**Merged pull requests:**

- s/OK/Gateway Timeout [\#250](https://github.com/k3po/k3po/pull/250) ([jitsni](https://github.com/jitsni))
- HTTP proxy sending 504 status code [\#245](https://github.com/k3po/k3po/pull/245) ([jitsni](https://github.com/jitsni))
- Fix for K3po issue \#223 [\#243](https://github.com/k3po/k3po/pull/243) ([cmebarrow](https://github.com/cmebarrow))
- Using HttpClientCodec to deal with head responses with Content-Length [\#242](https://github.com/k3po/k3po/pull/242) ([jitsni](https://github.com/jitsni))
- Defer resolving accept and connect options until needed by accept or connect behavior [\#240](https://github.com/k3po/k3po/pull/240) ([jfallows](https://github.com/jfallows))

## [3.0.0-alpha-1](https://github.com/k3po/k3po/tree/3.0.0-alpha-1) (2015-10-22)
[Full Changelog](https://github.com/k3po/k3po/compare/2.2.1...3.0.0-alpha-1)

**Merged pull requests:**

- Agrona transport \(requires Java8\) [\#238](https://github.com/k3po/k3po/pull/238) ([jfallows](https://github.com/jfallows))

## [2.2.1](https://github.com/k3po/k3po/tree/2.2.1) (2015-10-19)
[Full Changelog](https://github.com/k3po/k3po/compare/2.2.0...2.2.1)

**Closed issues:**

- Some WSE specification tests are not usable in practice because upstream requests may legitimately vary [\#232](https://github.com/k3po/k3po/issues/232)

**Merged pull requests:**

- Add wsx data exchange test [\#235](https://github.com/k3po/k3po/pull/235) ([cmebarrow](https://github.com/cmebarrow))
- k3po \#232: fix scripts in specification/wse/data to avoid assuming RECONNECT will [\#234](https://github.com/k3po/k3po/pull/234) ([cmebarrow](https://github.com/cmebarrow))

## [2.2.0](https://github.com/k3po/k3po/tree/2.2.0) (2015-10-15)
[Full Changelog](https://github.com/k3po/k3po/compare/2.1.1...2.2.0)

**Closed issues:**

- Can't build k3po on Windows: driver module gives test failures, script not found [\#230](https://github.com/k3po/k3po/issues/230)

**Merged pull requests:**

- Unnecessary .. in the script path [\#231](https://github.com/k3po/k3po/pull/231) ([jitsni](https://github.com/jitsni))
- Added WSN x-kaazing-idle-timeout k3po test scenarios [\#227](https://github.com/k3po/k3po/pull/227) ([irina-mitrea-luxoft](https://github.com/irina-mitrea-luxoft))

## [2.1.1](https://github.com/k3po/k3po/tree/2.1.1) (2015-10-12)
[Full Changelog](https://github.com/k3po/k3po/compare/2.1.0...2.1.1)

**Implemented enhancements:**

- specification: physical binds and connects [\#197](https://github.com/k3po/k3po/issues/197)

**Closed issues:**

- file transport fails to create file when parent directory does not exist [\#224](https://github.com/k3po/k3po/issues/224)

**Merged pull requests:**

- Normalize the path before passing to class loader to load resource [\#228](https://github.com/k3po/k3po/pull/228) ([jitsni](https://github.com/jitsni))
- Fix \#224 by ensuring that parent directory exists… [\#225](https://github.com/k3po/k3po/pull/225) ([jfallows](https://github.com/jfallows))
- Added x-kaazing-ping-pong WSN spec tests [\#221](https://github.com/k3po/k3po/pull/221) ([mgherghe](https://github.com/mgherghe))

## [2.1.0](https://github.com/k3po/k3po/tree/2.1.0) (2015-09-24)
[Full Changelog](https://github.com/k3po/k3po/compare/2.0.3...2.1.0)

**Closed issues:**

- Ability to wait and notify barriers from test framework. [\#215](https://github.com/k3po/k3po/issues/215)

**Merged pull requests:**

- Minor cleanup [\#220](https://github.com/k3po/k3po/pull/220) ([jitsni](https://github.com/jitsni))
- Updated community version to the latest [\#219](https://github.com/k3po/k3po/pull/219) ([dpwspoon](https://github.com/dpwspoon))
- resolves \#215: Ability to wait and notify barriers from test framework [\#218](https://github.com/k3po/k3po/pull/218) ([dpwspoon](https://github.com/dpwspoon))
- Fixed race condition in read vs write parameters [\#216](https://github.com/k3po/k3po/pull/216) ([dpwspoon](https://github.com/dpwspoon))
- File transport [\#213](https://github.com/k3po/k3po/pull/213) ([jitsni](https://github.com/jitsni))

## [2.0.3](https://github.com/k3po/k3po/tree/2.0.3) (2015-09-09)
[Full Changelog](https://github.com/k3po/k3po/compare/2.0.2...2.0.3)

**Merged pull requests:**

- Move scripts for socks5 specification project under main. [\#212](https://github.com/k3po/k3po/pull/212) ([cmebarrow](https://github.com/cmebarrow))
- Updated the spec to make X-WebSocket-Version header not mandatory in handshake response [\#211](https://github.com/k3po/k3po/pull/211) ([pkhanal](https://github.com/pkhanal))
- Adding RFC7232 Specification tests [\#207](https://github.com/k3po/k3po/pull/207) ([a-zuckut](https://github.com/a-zuckut))

## [2.0.2](https://github.com/k3po/k3po/tree/2.0.2) (2015-08-12)
[Full Changelog](https://github.com/k3po/k3po/compare/2.0.0...2.0.2)

**Implemented enhancements:**

- accept/connect should be able to take expression as a url [\#104](https://github.com/k3po/k3po/issues/104)

**Fixed bugs:**

- Occasional NPE in HttpClientChannelSink [\#98](https://github.com/k3po/k3po/issues/98)
- Non UTF-8 in scripts causes the finish on a control protocol to hang on junit [\#48](https://github.com/k3po/k3po/issues/48)

**Closed issues:**

- Specification.ws: Add new tests and scripts to FragmentationIT for client and server [\#195](https://github.com/k3po/k3po/issues/195)
- Specification.WSE - Should we relax the MUST requirement on upstream Content-Type header? [\#188](https://github.com/k3po/k3po/issues/188)

**Merged pull requests:**

- Add fin bit to fragmentation spec test [\#209](https://github.com/k3po/k3po/pull/209) ([chadpowers](https://github.com/chadpowers))
- Updated the wseb version to 1.0 to enable testing against current implementation [\#206](https://github.com/k3po/k3po/pull/206) ([pkhanal](https://github.com/pkhanal))
- Httpxe tests according to specifications. [\#205](https://github.com/k3po/k3po/pull/205) ([a-zuckut](https://github.com/a-zuckut))
- Added Extended Handshake Specification Tests [\#204](https://github.com/k3po/k3po/pull/204) ([a-zuckut](https://github.com/a-zuckut))
- Adding rfc7235 specifications [\#201](https://github.com/k3po/k3po/pull/201) ([a-zuckut](https://github.com/a-zuckut))
- Adding transport option for connect accept in the grammar to specify … [\#198](https://github.com/k3po/k3po/pull/198) ([jitsni](https://github.com/jitsni))
- Updated pom.xml to pickup the latest community also added animal-snif… [\#193](https://github.com/k3po/k3po/pull/193) ([sanjay-saxena](https://github.com/sanjay-saxena))
- Issue \#191: Added the missing line for the server to read the status … [\#192](https://github.com/k3po/k3po/pull/192) ([sanjay-saxena](https://github.com/sanjay-saxena))
- Support expression value in CONNECT and ACCEPT [\#190](https://github.com/k3po/k3po/pull/190) ([pkhanal](https://github.com/pkhanal))
- Added tests for reordering/denying extensions for Feature/new ws extension api [\#186](https://github.com/k3po/k3po/pull/186) ([dpwspoon](https://github.com/dpwspoon))

## [2.0.0](https://github.com/k3po/k3po/tree/2.0.0) (2015-04-03)
[Full Changelog](https://github.com/k3po/k3po/compare/2.0.0-alpha-9...2.0.0)

**Implemented enhancements:**

- support negative matches for header value based on an EL variable [\#143](https://github.com/k3po/k3po/issues/143)
- Backwards compatibility for control protocol version 1.0 clients [\#54](https://github.com/k3po/k3po/issues/54)
- Executable JAR with command line parameters [\#51](https://github.com/k3po/k3po/issues/51)
- Allow for barriers to signal a connect [\#29](https://github.com/k3po/k3po/issues/29)

**Fixed bugs:**

- Read/Write parameters causing NPE when write is requesting value that has already been read. [\#142](https://github.com/k3po/k3po/issues/142)
- No http method in request causes k3po to timeout and report writes that never made it on the wire [\#129](https://github.com/k3po/k3po/issues/129)

**Closed issues:**

- Multiple spaces between hex characters cause script parse errors [\#145](https://github.com/k3po/k3po/issues/145)
-  Server Hello World example script is missing connected after accepted [\#138](https://github.com/k3po/k3po/issues/138)
- Need to document property keyword in readme [\#114](https://github.com/k3po/k3po/issues/114)

**Merged pull requests:**

- Updated parent to latest version of community which required adding Java... [\#182](https://github.com/k3po/k3po/pull/182) ([dpwspoon](https://github.com/dpwspoon))
- Add specification.wsr from kaazing/specification.wsr [\#181](https://github.com/k3po/k3po/pull/181) ([dpwspoon](https://github.com/dpwspoon))
- Catchup merge from WSE [\#151](https://github.com/k3po/k3po/pull/151) ([dpwspoon](https://github.com/dpwspoon))
- Simplified examples and added delayed connect [\#148](https://github.com/k3po/k3po/pull/148) ([dpwspoon](https://github.com/dpwspoon))
- \#145: minor change to grammar to allow multiple spaces between hex chara... [\#146](https://github.com/k3po/k3po/pull/146) ([cmebarrow](https://github.com/cmebarrow))
- Combined java k3po based repositories [\#144](https://github.com/k3po/k3po/pull/144) ([dpwspoon](https://github.com/dpwspoon))
- K3PO Launcher [\#140](https://github.com/k3po/k3po/pull/140) ([pkhanal](https://github.com/pkhanal))

## [2.0.0-alpha-9](https://github.com/k3po/k3po/tree/2.0.0-alpha-9) (2015-03-02)
[Full Changelog](https://github.com/k3po/k3po/compare/2.0.0-alpha-8...2.0.0-alpha-9)

**Implemented enhancements:**

- Allow Http and Tcp accepts on same port/host [\#130](https://github.com/k3po/k3po/issues/130)
- Create K3poTestRule with same interface as K3poRule but without dependency on control protocol and k3po-maven-plugin  [\#117](https://github.com/k3po/k3po/issues/117)
- remove deprecated items [\#2](https://github.com/k3po/k3po/issues/2)

**Fixed bugs:**

- Http Methods do not throw script progress exception if the actual does not match expected [\#135](https://github.com/k3po/k3po/issues/135)
- K3po should throw and exception when Http and TCP attempt to accept on same host:port  [\#131](https://github.com/k3po/k3po/issues/131)
- \[HTTP Robot Lang\] Query string with multiple parameters throws Syntax error [\#118](https://github.com/k3po/k3po/issues/118)
- Windows Unit test failures [\#105](https://github.com/k3po/k3po/issues/105)
- HttpServerBootstrapTest\#shouldAcceptEchoThenClose occasionally failing on TravisCI [\#101](https://github.com/k3po/k3po/issues/101)
- Better exception on parse failures reported via robot control [\#43](https://github.com/k3po/k3po/issues/43)

**Closed issues:**

- A variable captured via a regex capture should be usable as an int. [\#134](https://github.com/k3po/k3po/issues/134)
- K3po maven-plugin can give better info level logging about current state [\#126](https://github.com/k3po/k3po/issues/126)
- Document how to run K3PO and ITs via JUnit from Eclipse IDE [\#99](https://github.com/k3po/k3po/issues/99)

**Merged pull requests:**

- Removed @Deprecated methods, resolves \#2 [\#137](https://github.com/k3po/k3po/pull/137) ([dpwspoon](https://github.com/dpwspoon))
- Removed itests module as all the tests have been moved to driver And Fix \#134 \(Regex variable capture type\) [\#133](https://github.com/k3po/k3po/pull/133) ([dpwspoon](https://github.com/dpwspoon))
- Fixed bug where HTTP requests and responses could not be only half specified [\#125](https://github.com/k3po/k3po/pull/125) ([dpwspoon](https://github.com/dpwspoon))
- Change debug output to format byte array in k3po language syntax [\#124](https://github.com/k3po/k3po/pull/124) ([jfallows](https://github.com/jfallows))
- Improve logging such that debug log is sufficient to diagnose script beh... [\#123](https://github.com/k3po/k3po/pull/123) ([jfallows](https://github.com/jfallows))
- resolves \#117 [\#122](https://github.com/k3po/k3po/pull/122) ([dpwspoon](https://github.com/dpwspoon))
- Make sure parsers bail instead of attempting to recover when grammar not... [\#121](https://github.com/k3po/k3po/pull/121) ([jfallows](https://github.com/jfallows))
- Resolve \#118 by adding & to the list of characters in a URL for the gram... [\#119](https://github.com/k3po/k3po/pull/119) ([jfallows](https://github.com/jfallows))

## [2.0.0-alpha-8](https://github.com/k3po/k3po/tree/2.0.0-alpha-8) (2015-01-29)
[Full Changelog](https://github.com/k3po/k3po/compare/k3po.parent-2.0.0-alpha-6...2.0.0-alpha-8)

**Fixed bugs:**

- Http State is being applied to tcp stream [\#112](https://github.com/k3po/k3po/issues/112)
- http status code is not validated [\#97](https://github.com/k3po/k3po/issues/97)
- HttpRobotBehaviorIT\#shouldAcceptWebsocketHandshake failing Test Timed out with a NPE in LocationInfo [\#55](https://github.com/k3po/k3po/issues/55)
- Assume.assumeTrue doesn't work in robot tests [\#41](https://github.com/k3po/k3po/issues/41)
- Add Limitations on Http Read/Write [\#27](https://github.com/k3po/k3po/issues/27)

**Closed issues:**

- \[HTTP Lang\] content-length of large inbound value is not calculated correctly [\#109](https://github.com/k3po/k3po/issues/109)
- \[HTTP Lang\] content-length for multiple outbound values \(with one or more large value\) is not calculated correctly [\#108](https://github.com/k3po/k3po/issues/108)
- Unable to combine scripts that both use 'property' definitions before streams [\#90](https://github.com/k3po/k3po/issues/90)
- HTTP connect can stall on read after Upgrade: websocket [\#88](https://github.com/k3po/k3po/issues/88)
- Add support for write masking [\#84](https://github.com/k3po/k3po/issues/84)
- Add HTTP support for `read header "Name" missing` [\#79](https://github.com/k3po/k3po/issues/79)
- Add HTTP language syntax for `write header host` [\#78](https://github.com/k3po/k3po/issues/78)
- Remove all references to "Rupert" [\#66](https://github.com/k3po/k3po/issues/66)

**Merged pull requests:**

- Updated pom files to pull in newest community, modified pom/plugins/chec... [\#116](https://github.com/k3po/k3po/pull/116) ([dpwspoon](https://github.com/dpwspoon))
- Fixes \#112 by allowing OPEN state in http visitor checks [\#113](https://github.com/k3po/k3po/pull/113) ([dpwspoon](https://github.com/dpwspoon))
- Resolve \#108 by increasing default maximum buffered content length [\#111](https://github.com/k3po/k3po/pull/111) ([jfallows](https://github.com/jfallows))
- Resolve \#109 by detecting incomplete request payload... [\#110](https://github.com/k3po/k3po/pull/110) ([jfallows](https://github.com/jfallows))
- Update JUEL dependency version [\#107](https://github.com/k3po/k3po/pull/107) ([jfallows](https://github.com/jfallows))
- Fix\#97 [\#102](https://github.com/k3po/k3po/pull/102) ([dpwspoon](https://github.com/dpwspoon))
- Added travis.ci yml build [\#96](https://github.com/k3po/k3po/pull/96) ([dpwspoon](https://github.com/dpwspoon))
- Avoid stack overflow when exception occurs during close. [\#92](https://github.com/k3po/k3po/pull/92) ([jfallows](https://github.com/jfallows))
- Resolve \#90 by relaxing grammar to support property sections... [\#91](https://github.com/k3po/k3po/pull/91) ([jfallows](https://github.com/jfallows))
- Resolve \#88 by replacing the HTTP response decoder... [\#89](https://github.com/k3po/k3po/pull/89) ([jfallows](https://github.com/jfallows))
- Remove unused import [\#87](https://github.com/k3po/k3po/pull/87) ([jfallows](https://github.com/jfallows))
- Resolve \#84 by adding support for 'write option mask ...' ... [\#86](https://github.com/k3po/k3po/pull/86) ([jfallows](https://github.com/jfallows))
- Detect insufficient bytes during read \(last\) byte array. [\#85](https://github.com/k3po/k3po/pull/85) ([jfallows](https://github.com/jfallows))
- Fail fast on partial header match. [\#83](https://github.com/k3po/k3po/pull/83) ([jfallows](https://github.com/jfallows))
- Resolve \#79 by adding support for 'read header "Name" missing' [\#82](https://github.com/k3po/k3po/pull/82) ([jfallows](https://github.com/jfallows))
- Improve \#78 by avoiding Host header by default... [\#81](https://github.com/k3po/k3po/pull/81) ([jfallows](https://github.com/jfallows))
- Resolve \#78 by adding support for HTTP language features... [\#80](https://github.com/k3po/k3po/pull/80) ([jfallows](https://github.com/jfallows))
- Add support for explicit 'write flush' used to flush HTTP headers... [\#77](https://github.com/k3po/k3po/pull/77) ([jfallows](https://github.com/jfallows))

## [k3po.parent-2.0.0-alpha-6](https://github.com/k3po/k3po/tree/k3po.parent-2.0.0-alpha-6) (2014-12-12)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-2.0.0-alpha-5...k3po.parent-2.0.0-alpha-6)

**Merged pull requests:**

- Update POMs and refactor Robot to K3PO [\#76](https://github.com/k3po/k3po/pull/76) ([jfallows](https://github.com/jfallows))

## [robot.parent-2.0.0-alpha-5](https://github.com/k3po/k3po/tree/robot.parent-2.0.0-alpha-5) (2014-12-11)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-2.0.0-alpha-4...robot.parent-2.0.0-alpha-5)

**Merged pull requests:**

- Updated scm to be accurate [\#75](https://github.com/k3po/k3po/pull/75) ([dpwspoon](https://github.com/dpwspoon))
- Removed robot.all due to javadoc/source not loading in eclipse and it no... [\#74](https://github.com/k3po/k3po/pull/74) ([dpwspoon](https://github.com/dpwspoon))
- Changed overall parent to community to facilitate automated builds [\#73](https://github.com/k3po/k3po/pull/73) ([dpwspoon](https://github.com/dpwspoon))

## [robot.parent-2.0.0-alpha-4](https://github.com/k3po/k3po/tree/robot.parent-2.0.0-alpha-4) (2014-12-10)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-2.0.0-alpha-3...robot.parent-2.0.0-alpha-4)

**Fixed bugs:**

- Finished is coming across twice on robot control abort [\#60](https://github.com/k3po/k3po/issues/60)

**Closed issues:**

- Regex matching does not support digit character classes [\#72](https://github.com/k3po/k3po/issues/72)
- Testing [\#71](https://github.com/k3po/k3po/issues/71)
- Parsed script omits header / parameter name \(shows value only\) [\#69](https://github.com/k3po/k3po/issues/69)
- Suport URI path segment parameters in accept and connect syntax [\#67](https://github.com/k3po/k3po/issues/67)
- Build on master and develop branches fail due to javadoc error [\#64](https://github.com/k3po/k3po/issues/64)
- Test WaffleIO / Kanbans [\#63](https://github.com/k3po/k3po/issues/63)
- Advice for write "Content-Length" inconsistent with Robot language grammar [\#45](https://github.com/k3po/k3po/issues/45)

**Merged pull requests:**

- Resolve \#69 by describing both names and values for write configuration,... [\#70](https://github.com/k3po/k3po/pull/70) ([jfallows](https://github.com/jfallows))
- Resolve \#67 by allowing semi-colon in URI literals in the script language [\#68](https://github.com/k3po/k3po/pull/68) ([jfallows](https://github.com/jfallows))
- Added fix for \#64 to keep building in spite of javadoc errors [\#65](https://github.com/k3po/k3po/pull/65) ([nowucca](https://github.com/nowucca))
- Modified robot all to use shade plugin [\#62](https://github.com/k3po/k3po/pull/62) ([dpwspoon](https://github.com/dpwspoon))
- Resolve \#60 by ensuring FINISHED message is sent at most once, ... [\#61](https://github.com/k3po/k3po/pull/61) ([jfallows](https://github.com/jfallows))
- Enhance script syntax to support overridable properties \#52 [\#59](https://github.com/k3po/k3po/pull/59) ([jfallows](https://github.com/jfallows))
- Added licensing for 3rd parties in NOTICE.txt [\#58](https://github.com/k3po/k3po/pull/58) ([dpwspoon](https://github.com/dpwspoon))

## [robot.parent-2.0.0-alpha-3](https://github.com/k3po/k3po/tree/robot.parent-2.0.0-alpha-3) (2014-10-20)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-2.0.0-alpha-2...robot.parent-2.0.0-alpha-3)

**Merged pull requests:**

- Master [\#57](https://github.com/k3po/k3po/pull/57) ([dpwspoon](https://github.com/dpwspoon))

## [robot.parent-2.0.0-alpha-2](https://github.com/k3po/k3po/tree/robot.parent-2.0.0-alpha-2) (2014-10-20)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-2.0.0-alpha-1...robot.parent-2.0.0-alpha-2)

**Implemented enhancements:**

- Support multiple scripts in @Robotic annotation [\#44](https://github.com/k3po/k3po/issues/44)

**Merged pull requests:**

- Candidate fix for issue \#55 NPE in LocationInfo at end of test. [\#56](https://github.com/k3po/k3po/pull/56) ([jfallows](https://github.com/jfallows))
- Add BBOSH server bootstrap \(polling strategy\) ... [\#50](https://github.com/k3po/k3po/pull/50) ([jfallows](https://github.com/jfallows))

## [robot.parent-2.0.0-alpha-1](https://github.com/k3po/k3po/tree/robot.parent-2.0.0-alpha-1) (2014-10-16)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-1.4.0.1...robot.parent-2.0.0-alpha-1)

**Merged pull requests:**

- HTTP transport enhancements [\#49](https://github.com/k3po/k3po/pull/49) ([jfallows](https://github.com/jfallows))
- Add a Gitter chat badge to README.md [\#47](https://github.com/k3po/k3po/pull/47) ([gitter-badger](https://github.com/gitter-badger))

## [robot.parent-1.4.0.1](https://github.com/k3po/k3po/tree/robot.parent-1.4.0.1) (2014-10-02)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-1.3.0.2...robot.parent-1.4.0.1)

## [robot.parent-1.3.0.2](https://github.com/k3po/k3po/tree/robot.parent-1.3.0.2) (2014-09-19)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-1.3.0.1...robot.parent-1.3.0.2)

**Implemented enhancements:**

- Provide Http Robot Control Protocol [\#21](https://github.com/k3po/k3po/issues/21)
- Upgrade Robot Driver and Control to Use Netty 4.x rather than Netty 3.9.0.Final [\#9](https://github.com/k3po/k3po/issues/9)
- Support for testing javascript applications [\#7](https://github.com/k3po/k3po/issues/7)
- robot-junit references robot-maven-plugin directly [\#1](https://github.com/k3po/k3po/issues/1)

## [robot.parent-1.3.0.1](https://github.com/k3po/k3po/tree/robot.parent-1.3.0.1) (2014-09-15)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-1.3.0.0...robot.parent-1.3.0.1)

## [robot.parent-1.3.0.0](https://github.com/k3po/k3po/tree/robot.parent-1.3.0.0) (2014-09-15)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-0.0.0.13...robot.parent-1.3.0.0)

## [robot.parent-0.0.0.13](https://github.com/k3po/k3po/tree/robot.parent-0.0.0.13) (2014-09-15)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-0.0.0.12...robot.parent-0.0.0.13)

**Merged pull requests:**

- ServiceLoader.load\(Class\) changes and Java \<\> syntax [\#42](https://github.com/k3po/k3po/pull/42) ([jitsni](https://github.com/jitsni))

## [robot.parent-0.0.0.12](https://github.com/k3po/k3po/tree/robot.parent-0.0.0.12) (2014-08-25)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-0.0.0.11...robot.parent-0.0.0.12)

**Merged pull requests:**

- Support literal dashes in regular expressions... [\#38](https://github.com/k3po/k3po/pull/38) ([jfallows](https://github.com/jfallows))

## [robot.parent-0.0.0.11](https://github.com/k3po/k3po/tree/robot.parent-0.0.0.11) (2014-08-23)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-0.0.0.10...robot.parent-0.0.0.11)

**Merged pull requests:**

- Use test classpath to load scripts in driver... [\#37](https://github.com/k3po/k3po/pull/37) ([jfallows](https://github.com/jfallows))
- Using Objects.equals\(\) to simplify the equals\(\) logic [\#36](https://github.com/k3po/k3po/pull/36) ([jitsni](https://github.com/jitsni))
- Move script resolution from Robot test integration to Robot ... [\#35](https://github.com/k3po/k3po/pull/35) ([jfallows](https://github.com/jfallows))

## [robot.parent-0.0.0.10](https://github.com/k3po/k3po/tree/robot.parent-0.0.0.10) (2014-08-15)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-0.0.0.9...robot.parent-0.0.0.10)

**Merged pull requests:**

- fix test failures [\#34](https://github.com/k3po/k3po/pull/34) ([jeff-2](https://github.com/jeff-2))
- Resolve relative script names via class loader instead of filesystem to ... [\#33](https://github.com/k3po/k3po/pull/33) ([jfallows](https://github.com/jfallows))

## [robot.parent-0.0.0.9](https://github.com/k3po/k3po/tree/robot.parent-0.0.0.9) (2014-08-15)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-0.0.0.8...robot.parent-0.0.0.9)

**Implemented enhancements:**

- Google Test integration [\#5](https://github.com/k3po/k3po/issues/5)

**Closed issues:**

- Modify robot control protocol to support versioning and to pass robot script location [\#6](https://github.com/k3po/k3po/issues/6)

**Merged pull requests:**

- Revert "Fix JSON encoding issues" [\#32](https://github.com/k3po/k3po/pull/32) ([dpwspoon](https://github.com/dpwspoon))
- Fix JSON encoding issues [\#31](https://github.com/k3po/k3po/pull/31) ([jeff-2](https://github.com/jeff-2))
- Http Response Format Fix [\#30](https://github.com/k3po/k3po/pull/30) ([jeff-2](https://github.com/jeff-2))
- Examples [\#28](https://github.com/k3po/k3po/pull/28) ([dpwspoon](https://github.com/dpwspoon))

## [robot.parent-0.0.0.8](https://github.com/k3po/k3po/tree/robot.parent-0.0.0.8) (2014-08-06)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-0.0.0.7...robot.parent-0.0.0.8)

**Merged pull requests:**

- Http robot control [\#25](https://github.com/k3po/k3po/pull/25) ([jeff-2](https://github.com/jeff-2))

## [robot.parent-0.0.0.7](https://github.com/k3po/k3po/tree/robot.parent-0.0.0.7) (2014-08-06)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-0.0.0.6...robot.parent-0.0.0.7)

**Fixed bugs:**

- Support Escaping \" and all documented characters  [\#20](https://github.com/k3po/k3po/issues/20)

**Merged pull requests:**

- Http robot control [\#24](https://github.com/k3po/k3po/pull/24) ([jeff-2](https://github.com/jeff-2))
- Http robot control [\#23](https://github.com/k3po/k3po/pull/23) ([jeff-2](https://github.com/jeff-2))

## [robot.parent-0.0.0.6](https://github.com/k3po/k3po/tree/robot.parent-0.0.0.6) (2014-07-29)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-0.0.0.5...robot.parent-0.0.0.6)

**Merged pull requests:**

- Http robot control [\#19](https://github.com/k3po/k3po/pull/19) ([jeff-2](https://github.com/jeff-2))

## [robot.parent-0.0.0.5](https://github.com/k3po/k3po/tree/robot.parent-0.0.0.5) (2014-07-25)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-0.0.0.4...robot.parent-0.0.0.5)

**Merged pull requests:**

- Robot protocol [\#18](https://github.com/k3po/k3po/pull/18) ([dpwspoon](https://github.com/dpwspoon))

## [robot.parent-0.0.0.4](https://github.com/k3po/k3po/tree/robot.parent-0.0.0.4) (2014-07-24)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-0.0.0.3...robot.parent-0.0.0.4)

**Merged pull requests:**

- Pushing code cleanup changes [\#17](https://github.com/k3po/k3po/pull/17) ([dpwspoon](https://github.com/dpwspoon))
- Moved all java files to org.kaazing.robot.{submodule}.   This is because... [\#16](https://github.com/k3po/k3po/pull/16) ([dpwspoon](https://github.com/dpwspoon))

## [robot.parent-0.0.0.3](https://github.com/k3po/k3po/tree/robot.parent-0.0.0.3) (2014-07-23)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-0.0.0.2...robot.parent-0.0.0.3)

**Implemented enhancements:**

- Fold robot-control into robot-junit [\#3](https://github.com/k3po/k3po/issues/3)

**Merged pull requests:**

- C robot control [\#15](https://github.com/k3po/k3po/pull/15) ([jeff-2](https://github.com/jeff-2))

## [robot.parent-0.0.0.2](https://github.com/k3po/k3po/tree/robot.parent-0.0.0.2) (2014-07-19)
[Full Changelog](https://github.com/k3po/k3po/compare/robot.parent-0.0.0.1...robot.parent-0.0.0.2)

## [robot.parent-0.0.0.1](https://github.com/k3po/k3po/tree/robot.parent-0.0.0.1) (2014-07-19)
**Closed issues:**

- test [\#4](https://github.com/k3po/k3po/issues/4)

**Merged pull requests:**

- Robot regex fix [\#14](https://github.com/k3po/k3po/pull/14) ([dpwspoon](https://github.com/dpwspoon))
- Very basic examples [\#12](https://github.com/k3po/k3po/pull/12) ([nowucca](https://github.com/nowucca))



\* *This Change Log was automatically generated by [github_changelog_generator](https://github.com/skywinder/Github-Changelog-Generator)*