# 사전

## 사용한 기술

- clojure
- polylith / 구조에 대해 주장이 있는 모노레포 도구
- integrant / 설정 및 의존성 구성
- ring (with jetty adaptor) / 웹 서버

## 예정

- components/ndic: [네이버 사전](https://en.dict.naver.com/api3/enko/search?query=test&m=mobile&shouldSearchVlive=false&lang=ko) 응답 파싱하여 사용하기 쉬운 형태로 가공
- bases/backend: ndic 응답을 GraphQL 쿼리로 구현
- lacinia 쿼리 구현 및 superlifter 적용, honeysql + jdbc + sqlite 캐시 구현
- clj-kondo 린트 적용
- kaocha 테스트 러너 적용
- 배포를 위한 uberjar 생성 (욕심을 내본다면 GraalVM 네이티브 이미지)

## 개발 환경

```sh
$ clj -A:dev
[main] INFO org.eclipse.jetty.util.log - Logging initialized @1528ms to org.eclipse.jetty.util.log.Slf4jLog
Clojure 1.11.1
user=> (go)
[main] INFO org.eclipse.jetty.server.Server - jetty-9.4.44.v20210927; built: 2021-09-27T23:02:44.612Z; git: 8da83308eeca865e495e53ef315a249d63ba9332; jvm 18.0.1+0
[main] INFO org.eclipse.jetty.server.AbstractConnector - Started ServerConnector@6c830ebd{HTTP/1.1, (http/1.1)}{0.0.0.0:8000}
[main] INFO org.eclipse.jetty.server.Server - Started @7601ms
:initiated
user=>
```

## API

- `curl http://localhost:8000/?word=test`
