# ffc-java-server-sdk-wrapper

This projet capsulates ffc server sdk, supports the evaluation of a feature flag by a given user or all feature flags in
a specified environment.

## Install and Run

### Docker Image

```
docker pull featureflagco/java-server-sdk-wrapper:latest
```

### How to use this image

#### Configuration

Requires the following environment variable

`envSecret` the environment key for your environment set in featureflag.co. This value is mandatory. if ignore this
variable, an exception will be thrown in the startup process.

`offline` if in offline mode - if true is set, the SDK wrapper will not make network connections to featureflag.co for
any purpose. In this case you should initialize the feature flags in using `dataFile` or sending all the feature flags
you need by `init` API; if false is set, the SDK will attempt to make network connections to featureflag.co and update
the feature flags in streaming mode. Note that `init` API and `dataFile` can't work any more in online mode. The default
value is **_true_**, in **_offline_** mode

`dataFile` configures the position that the used feature flags will be stored. In the docker container, the default
value is **_/data/data.json_**. If the file has already existed, feature flags will be automatically loaded in the
startup process. You can mount a json file in the external systems in order to initialize feature flags when the SDK
wrapper starts up. We strongly recommend you to initialize feature flags in using **_init_** API, and featureflag.co
provide an UI to do this.

`serverPort`: the embedded server starts on default port **8080**.

`healthPort`: the health check on default port **8081**.

`JAVA_OPTS`  you want to have the option to add Java command line options at runtime. For example, 
java jvm options to launch this app by running the following command:

```
docker run -it -p 8080:8080 -e envSecret={your-sdk-key} -e JAVA_OPTS=-Xmx128m featureflagco/java-server-sdk-wrapper:latest
```

#### Run this Image

you can simply run the app like the following command:

```
docker run -it -p 8080:8080 -e envSecret={your-sdk-key} featureflagco/java-server-sdk-wrapper:latest
```

`dataFile` and `offline` is optional, but it's possible to mount into container and/or to work online, for example:

```
 docker run -it -p 8080:8080 -v {your-path}:/data -e envSecret={your-envSecret} featureflagco/java-server-sdk-wrapper:latest
```
```
 docker run -it -p 8080:8080 -e offline=false -e envSecret={your-envSecret} featureflagco/java-server-sdk-wrapper:latest
```

## API

**_All the requests need a envSecret header, if not existed in header, you will receive a http 400 error; if the envSecret
doesn't match the one in SDK wrapper, you will receive a http 401 error; if invalid request posted, you will receive a http 400 error_**

`/api/public/feature-flag/variation`: post method, evaluates a flag value by a given user and a flag key name


Request Body:

```
{
  "featureFlagKeyName": {flag-keyu-name}, #mandatory
  "userKeyId": {user-key-id}, #mandatory
  "userName": {username}, #mandatory
  "email": {email}, #optional
  "country": {country}, #optional
  "customizedProperties": [ #optional
    {
      "name": {name},
      "value": {value}
    }
  ]
}
```

Response:

`variation`: the flag value

`reason` explains how to evaluate the flag value or shows the error info

if `id` = -1, it means flag not found or an error; if an error occurs, `success` = false and `message` shows also the
error info.

```
{
    "data": {
        "variation": {flag-value},
        "id": {id},
        "reason": {reason},
        "name": {flag name},
        "keyName": {flag key name}
    },
    "success": true,
    "message": "OK"
}
```

`/api/public/feature-flag/variations` post method, get all the flag values from SDK wrapper.

Request Body:

```
{
  "userKeyId": {user-key-id}, #mandatory
  "userName": {username}, #mandatory
  "email": {email}, #optional
  "country": {country}, #optional
  "customizedProperties": [ #optional
    {
      "name": {name},
      "value": {value}
    }
  ]
}
```

Response:

if `id` = -1, it means flag not found or an error; if an error occurs, `success` = false and `message` shows also the
error info.

```
{
    "data": [
        {
            "variation": {flag-value},
            "id": {id},
            "reason": {reason},
            "name": {flag name},
            "keyName": {flag key name}
        },
        ...
    ],
    "success": true,
    "message": "OK"
}
```

`/api/public/feature-flag/init`: post method, initializes the feature flags in SDK wrapper.

you should contact us to get a json file, that contains the latest feature flags in your environment, or use data push server in featureflag.co


Response:

```
{
    "success": true,
    "message": "Initialization well done"
}
```

`/health`: get method(default port 8081), health check. if UP, HTTP status is 200; if DOWN, HTTP status is 503

Response:

```
{
  "status": {
    "code": "UP",
    "description": {description}
  }
  ...
}
```
Do not forget to publish healthPort(8081 by default), if you want health check out of docker container.