This project uses [K3PO](http://github.com/k3po/k3po) to help
[HTTP Multi-Factor Authentication](./SPEC.md) implementations automate their verification of the necessary requirements.

## Overview of Multi-Factor Authentication for process of using the Sec-Challenge-Identity 

This section is nonnormative

Each factor defined as a different realm in gateway-config.xml.
Service associated with multiple realms, in order of factors to be applied.

HttpAcceptor detects multiple factors via presence of multiple realm names in configuration.
If multi-factor, then use Sec-Challenge-Identity header to look up Subject + realm index from ExpiringState.
If missing, start from realm index zero (first factor).
Process LoginContext for current realm index, passing existing Subject, if present.
If current realm succeeds then process next realm with same Subject.
If current realm > 0 fails, then:
Store Subject + current realm index in ExpiringState by UUID
Send challenge to client, including Sec-Challenge-Identity set to UUID.
If current realm == 0 fails, then:
Send challenge to client

HttpConnector detects presence of Sec-Challenge-Identity header in 401 response and includes Sec-Challenge-Identity header with the same value in the subsequent repeated request.

If shared state – e.g. username at key "javax.security.auth.login.name" – needs to be propagated from factor 1 realm to factor 2 realm then a LoginModule at the beginning of factor 2 can extract information as needed from the Subject and inject it into shared state. 
