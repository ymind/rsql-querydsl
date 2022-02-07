# 更新日志

## 1.5.3 (2022-02-07)

### Features

- add PathFactory ([cc35befa](https://github.com/ymind/rsql-querydsl/commit/cc35befa4e2bdbe91597bb816778c2be817623d8))
- allow set from clause when build JPA query ([be9c5475](https://github.com/ymind/rsql-querydsl/commit/be9c54755840ff4839c8c689543214e687e4bdef))


### Code Refactoring

- normalize sort property naming ([dbbe6148](https://github.com/ymind/rsql-querydsl/commit/dbbe614848b1660e19a01feef2be8ca1fac5b748))
- refactor FieldMetadata ([e4625c4c](https://github.com/ymind/rsql-querydsl/commit/e4625c4ccca559c85701da071a1f515c7ff6562c))


### Chores

- code cleanup ([962655f1](https://github.com/ymind/rsql-querydsl/commit/962655f17b8c735242e97167adf9bfe42fb0f849))
- code cleanup ([6a01bdca](https://github.com/ymind/rsql-querydsl/commit/6a01bdca5fa441839741cf8d3124b78a52a8e912))


### Build System

- **gradle/plugin**: bumped com.github.ben-manes.versions version to 0.42.0 ([09292b03](https://github.com/ymind/rsql-querydsl/commit/09292b03f10c655b5bea5f010b2edc1ad35162b2))


## 1.3.3 (2022-01-30)

### Bug Fixes

- fix `Projections.*` not work in select expressions ([ed805716](https://github.com/ymind/rsql-querydsl/commit/ed80571663da64c9b8a97d02acde85f81feab120))


### Features

- support custom from variable ([d6f00a2b](https://github.com/ymind/rsql-querydsl/commit/d6f00a2bada0b9d4628d2c3d62e815159328b996))
- full support for select ([843ecfdd](https://github.com/ymind/rsql-querydsl/commit/843ecfdd0dd0190c0f705cc09c8224bbe51fc980))


### Code Refactoring

- optimize select expression support ([00113fc3](https://github.com/ymind/rsql-querydsl/commit/00113fc31914ada0840f3f2f1aaee7f926da6423))
- remove `noPaging` parameter from buildJPAQuery ([0fc71996](https://github.com/ymind/rsql-querydsl/commit/0fc71996f08cc162b227db8b9257ff8a16e111ec))
- add time patterns for DateUtil ([5dca322b](https://github.com/ymind/rsql-querydsl/commit/5dca322bc180273c444776b2334f5a42d8b43329))


### Chores

- code cleanup ([464bc36d](https://github.com/ymind/rsql-querydsl/commit/464bc36df1172f2d07ff62779f3a64feb8c15786))


## 1.1.1 (2022-01-28)

### Bug Fixes

- `selectFieldPath` is not work ([9b89cf06](https://github.com/ymind/rsql-querydsl/commit/9b89cf06017d1509dd7b28d6a5efe572fae4e42c))


## 1.1.0 (2022-01-28)

### BREAKING CHANGES

- rename `buildQuery` to `buildJPAQuery` ([27d5064d](https://github.com/ymind/rsql-querydsl/commit/27d5064dfab49f143d9059335d6eb26624b54c4c))
- delete `QuerydslRsql<E>.fetchOne()` ([631d1c0b](https://github.com/ymind/rsql-querydsl/commit/631d1c0b174f9104ad5ed1e03f4f60f63a0da095))
- delete `QuerydslRsql<E>.fetch()` ([3b35382e](https://github.com/ymind/rsql-querydsl/commit/3b35382ed6651599598e244ec58f811e992e1cc3))


### Features

- support multiple select expressions ([e646e208](https://github.com/ymind/rsql-querydsl/commit/e646e208056cc7639a973527bab1080a85b86492))


### Chores

- **deps**: bumped spring version to 2.6.2 ([be6d5870](https://github.com/ymind/rsql-querydsl/commit/be6d587054246cdfb18f09799b04965c6fecb414))
- **deps**: bumped jackson-module-kotlin version to 2.13.1 ([d1dd4a80](https://github.com/ymind/rsql-querydsl/commit/d1dd4a806ebc539219662053ee08fb49b9c13b5c))
- **deps**: bumped h2 version to 2.0.204 ([b7d44852](https://github.com/ymind/rsql-querydsl/commit/b7d44852921437a1113794df03e149eecbaa01f1))
- **deps**: bumped h2 version to 2.1.210 ([511381a4](https://github.com/ymind/rsql-querydsl/commit/511381a4a96a79314ff2adfb819252f71fc78100))
- **deps**: bumped spring version to 2.6.3 ([6569a302](https://github.com/ymind/rsql-querydsl/commit/6569a3025ff8948a0f7adc16e8b7bf3d5155bc5a))


### Build System

- **gradle**: bumped gradle wrapper version to 7.3.3 ([8c303e29](https://github.com/ymind/rsql-querydsl/commit/8c303e2975064fe963c758f47eb0793fd218aa39))
- **kotlin**: bumped kotlin version from to 1.6.10 ([62e5a492](https://github.com/ymind/rsql-querydsl/commit/62e5a492df86a088a9c5802bc9f76d0bada9b4de))
- **gradle/plugin**: bumped org.jlleitschuh.gradle.ktlint version to 10.2.1 ([a7585aa4](https://github.com/ymind/rsql-querydsl/commit/a7585aa4663dff20f28267387dfcc37ae9c1b01e))
- **gradle/plugin**: bumped se.patrikerdes.use-latest-versions version to 0.2.18 ([d04b2337](https://github.com/ymind/rsql-querydsl/commit/d04b2337f617e02b31f85cccf49e2216309b2712))
- **gradle/plugin**: bumped com.github.ben-manes.versions version to 0.41.0 ([57d3a0b6](https://github.com/ymind/rsql-querydsl/commit/57d3a0b60424cd53897ea92dce1036c9dd1a7bb6))


## 0.7.11 (2021-10-27)

### Code Refactoring

- remove `FunctionTypeHandler` ([49966547](https://github.com/ymind/rsql-querydsl/commit/4996654770efb8721c542019f133ace6808be37a))


### Chores

- **deps**: bumped spring version to 2.5.6 ([7139ac4a](https://github.com/ymind/rsql-querydsl/commit/7139ac4a477f35200a5910c770f2f2d988f49dc2))
- **deps**: bumped jackson-module-kotlin version to 2.13.0 ([22f27daa](https://github.com/ymind/rsql-querydsl/commit/22f27daa5ad4ccfe42b40d098eeb2e98303b74b0))
- code cleanup ([7e2df15d](https://github.com/ymind/rsql-querydsl/commit/7e2df15d5f8b52a5cb43c4e0c2ea7d403cab7ea6))


### Build System

- **chore**: bumped querydsl version to 5.0.0 ([9323216f](https://github.com/ymind/rsql-querydsl/commit/9323216f47d5cc132f26eb47055d675c0851b47d))
- **gradle**: bumped gradle wrapper version to 7.2 ([31519600](https://github.com/ymind/rsql-querydsl/commit/3151960067e110c1e6ed71efa5ebfc8481493af4))
- **kotlin**: bumped kotlin version from to 1.5.31 ([ae84c9b4](https://github.com/ymind/rsql-querydsl/commit/ae84c9b48d5b530c8f5b375703e0a2d8e2e102d6))
- **gradle/plugin**: bumped org.jlleitschuh.gradle.ktlint version to 10.2.0 ([0faf97f3](https://github.com/ymind/rsql-querydsl/commit/0faf97f3c59c544c6ee652b574a8e2d197a7ed96))


## 0.7.3 (2021-07-12)

### Features

- support rsql node interceptor ([b2d74266](https://github.com/ymind/rsql-querydsl/commit/b2d74266131a057f2aa1a78a7286a899e5c10eb6))


### Code Refactoring

- **common**: remove FieldNotSupportedException ([56bb0aed](https://github.com/ymind/rsql-querydsl/commit/56bb0aed42d8fee331ea67cdab59db5c393cafbc))
- optimize entityClass acquisition mechanism ([fdae805c](https://github.com/ymind/rsql-querydsl/commit/fdae805c1d95f62ea6c66431db9f50356d5656ae))


### Chores

- **deps**: bumped jackson-module-kotlin version to 2.12.4 ([a3fb9c6d](https://github.com/ymind/rsql-querydsl/commit/a3fb9c6d62fb9deb973aec13c5eeff5e5e584ac7))
- **deps**: bumped spring version to 2.5.2 ([84d8f740](https://github.com/ymind/rsql-querydsl/commit/84d8f74072d0746a95cd2e24466d2cab3bc61671))


### Build System

- **gradle**: bumped gradle wrapper version to 7.1.1 ([6c5f931d](https://github.com/ymind/rsql-querydsl/commit/6c5f931dc6707455b4214bcdab5e2fc485d4acc7))
- **kotlin**: bumped kotlin version from to 1.5.10 ([f0365224](https://github.com/ymind/rsql-querydsl/commit/f0365224e8c0447b52cf7e1c0d71096e18ad4000))
- **gradle/plugin**: bumped com.github.ben-manes.versions version to 0.39.0 ([b9d8788d](https://github.com/ymind/rsql-querydsl/commit/b9d8788d67b9799958c9abdfc47f6bd750f0c0f5))
- **gradle/plugin**: bumped se.patrikerdes.use-latest-versions version to 0.2.17 ([64811fd2](https://github.com/ymind/rsql-querydsl/commit/64811fd2cc7dfe9cdc06612bd01aac4ca49625a2))
- **gradle/plugin**: bumped org.jlleitschuh.gradle.ktlint version to 10.1.0 ([27db73b9](https://github.com/ymind/rsql-querydsl/commit/27db73b9a516fbbddf1d1d6eaacca87b7134ef15))


## 0.6.0 (2021-05-20)

### Features

- support auto detect datetime format ([a9b75691](https://github.com/ymind/rsql-querydsl/commit/a9b75691aca805cb28115698b1d270a7d279e416))


### Build System

- **gradle**: bumped gradle wrapper version to 7.0.2 ([e11163ac](https://github.com/ymind/rsql-querydsl/commit/e11163ac933fce0a3f43ea8f97803e5da2109e5d))


## 0.5.22 (2021-05-10)

### Bug Fixes

- `globalPredicate` not work when `where` is null ([e8aa876f](https://github.com/ymind/rsql-querydsl/commit/e8aa876f30d95612da8641dfc6ceac43f2a07d24))


### Code Refactoring

- **util**: support `yyyy-MM-dd'T'HH:mm:ss.SSS` date format ([4cc949b0](https://github.com/ymind/rsql-querydsl/commit/4cc949b042c7a685c1076be27e7051cc811dae26))
- upgrade deprecated toLowerCase() method ([5f5c592a](https://github.com/ymind/rsql-querydsl/commit/5f5c592a91364ab84530dfe896e3a7dc52318439))


### Chores

- **deps**: upgrade spring version to 2.4.5 ([9e7bbeca](https://github.com/ymind/rsql-querydsl/commit/9e7bbecae41d7f64113cc55e52d8464d314830a1))
- **deps**: upgrade jackson-module-kotlin version to 2.12.3 ([51c618af](https://github.com/ymind/rsql-querydsl/commit/51c618afe44bb7b813f61a3bb21ed1791c0cca0c))
- **deps**: upgrade commons-lang3 version to 3.12.0 ([ea90fc5f](https://github.com/ymind/rsql-querydsl/commit/ea90fc5f7aea6366001c56e26660863bd9468dde))


### Build System

- **gradle**: bumped gradle wrapper version to 7.0 ([610a234b](https://github.com/ymind/rsql-querydsl/commit/610a234b357e851c98ad6a282349fb8bf7ecac6b))
- **kotlin**: bumped kotlin version from to 1.5.0 ([f6b37888](https://github.com/ymind/rsql-querydsl/commit/f6b378885a288b2be8a81ba6c244b221d9a2995b))
- **gradle/plugin**: upgrade com.github.ben-manes.versions version to 0.38.0 ([f2876820](https://github.com/ymind/rsql-querydsl/commit/f2876820d4c111e20a4156771256227e36e6fda7))
- **gradle/plugin**: upgrade se.patrikerdes.use-latest-versions version to 0.2.16 ([732cb1e0](https://github.com/ymind/rsql-querydsl/commit/732cb1e0730bdd23b3a5d5ca3f3c9fb73428dac3))
- **gradle/plugin**: upgrade org.jlleitschuh.gradle.ktlint version to 10.0.0 ([543fe50a](https://github.com/ymind/rsql-querydsl/commit/543fe50ad356675dd52308453ebc8c8dc332477e))


## 0.5.11 (2021-02-01)

### Bug Fixes

- when has only a single value, the `in` operation will throw an exception ([2e748d22](https://github.com/ymind/rsql-querydsl/commit/2e748d225c4f42604bd6edf0586deaf688d53ee1))


### Chores

- **deps**: bumped spring version from 2.3.4.RELEASE to 2.4.2 ([5dc0c3c7](https://github.com/ymind/rsql-querydsl/commit/5dc0c3c73aefb250e63c3ced28f33c48ff3bc9a0))
- **deps**: bumped jackson-module-kotlin version from 2.11.3 to 2.12.1 ([4edce4cc](https://github.com/ymind/rsql-querydsl/commit/4edce4cc9eb1af9df1f1b05de63e1b9c74450318))
- **gradle**: add use-latest-versions plugin ([f00b578f](https://github.com/ymind/rsql-querydsl/commit/f00b578fbbf8fa65944a82aab05a991fa80b9057))


### Build System

- **gradle**: bumped gradle wrapper version from 6.6.1 to 6.8.1 ([f279f322](https://github.com/ymind/rsql-querydsl/commit/f279f322ec06daaa4f767fe753d96b4700c401c5))
- **kotlin**: bumped kotlin version from 1.4.10 to 1.4.21-2 ([c204e14d](https://github.com/ymind/rsql-querydsl/commit/c204e14d76c9427fdfcfe2e8f01821e013d08bfa))


## 0.5.5 (2020-10-09)

### BREAKING CHANGES

- rename `selectFrom` to `from` ([18be5930](https://github.com/ymind/rsql-querydsl/commit/18be59302ca8d89b45af18de94ffb31b7cb60454))
- rename `size` to `limit` ([7cfff03c](https://github.com/ymind/rsql-querydsl/commit/7cfff03c544e283ff95fc9f1c0901433d79e2fd7))
- remove `page-string` and `limit-string` support ([289e780a](https://github.com/ymind/rsql-querydsl/commit/289e780a2ed0e24a8c13e9ecda680599703d887a))


### Bug Fixes

- **common**: fix `FieldNotSupportedException` arguments type ([1d8497aa](https://github.com/ymind/rsql-querydsl/commit/1d8497aa71e1a636cf4e4839af1f6557ae85e458))
- QuerydslRsql.buildPredicate() return null when globalPredicate is null ([ee0c1191](https://github.com/ymind/rsql-querydsl/commit/ee0c11913899e95c1140859831a84e354aa5f84a))


### Features

- support custom entity field type handler ([063203a0](https://github.com/ymind/rsql-querydsl/commit/063203a00d26c694d1e20de24a36e5cddbf49b4e))


### Performance Improvements

- **util**: enhance DateUtil ([e59fcc40](https://github.com/ymind/rsql-querydsl/commit/e59fcc40afe374a7b368fbe8c2f706fd30581016))


### Code Refactoring

- rename `RsqlConfig.getFieldTypeHandlers` to `RsqlConfig.addFieldTypeHandlers` ([e52044dc](https://github.com/ymind/rsql-querydsl/commit/e52044dc7025fc502ebee30787981d88d4300a62))
- make regexOptions private ([f254b81a](https://github.com/ymind/rsql-querydsl/commit/f254b81a1344000f6f49760b5b27507c6d0d54d4))
- make EntityManager none null ([882d5ade](https://github.com/ymind/rsql-querydsl/commit/882d5adeacbb72be7377a959309c124f249c98c6))
- optimize type handlers ([758a5abc](https://github.com/ymind/rsql-querydsl/commit/758a5abcc7bd98d8868a1a5e350dac60f7a78aad))
- remove name field from RsqlOperator ([64ab32bc](https://github.com/ymind/rsql-querydsl/commit/64ab32bcdbbd5723387b0662dcb5c04d53066c08))
- typo fix and code cleanup ([0e5e4d09](https://github.com/ymind/rsql-querydsl/commit/0e5e4d092eab0de781ded2aefc32e9171676a081))
- split SortFieldTypeHandler ([09b9a36c](https://github.com/ymind/rsql-querydsl/commit/09b9a36c1e4d339ad06a769d487b42e2c039913e))


### Chores

- **bumped**: remove versions plugins ([c9e4c0a7](https://github.com/ymind/rsql-querydsl/commit/c9e4c0a70a6971ae445f7a70bfaff8df4755f029))
- **deps**: bumped spring boot from 2.3.0.RELEASE to 2.3.1.RELEASE ([4560b4be](https://github.com/ymind/rsql-querydsl/commit/4560b4be13fbb1eb221ba06e4b721c977ddaf399))
- **deps**: bumped spring boot from 2.3.1.RELEASE to 2.3.2.RELEASE ([6acd26b5](https://github.com/ymind/rsql-querydsl/commit/6acd26b59dc7010e26a5a040be0e325854586782))
- **deps**: bumped jackson-module-kotlin from 2.11.1 to 2.11.2 ([9028d3cc](https://github.com/ymind/rsql-querydsl/commit/9028d3cc13e9dea97fc5e9d76b553f1c16f51bcf))
- **deps**: bumped commons-lang3 from 3.10 to 3.11 ([116b18ec](https://github.com/ymind/rsql-querydsl/commit/116b18ec1f3ef310d18f494b688726f9a01fac92))
- **deps**: bumped spring version from 2.3.2.RELEASE to 2.3.3.RELEASE ([92b861d3](https://github.com/ymind/rsql-querydsl/commit/92b861d3f6408da16ce4dd5c7ad17ef36799bfe0))
- **deps**: bumped spring version from 2.3.3.RELEASE to 2.3.4.RELEASE ([5fa1ab45](https://github.com/ymind/rsql-querydsl/commit/5fa1ab455a4041a58ee696ce2ea1a3f325b4a9ee))
- **gradle**: bumped team.yi.semantic-gitlog from 0.5.3 to 0.5.12 ([16361c76](https://github.com/ymind/rsql-querydsl/commit/16361c76a1f47b4a6c0bbe39eaf7163a2e02387b))
- **gradle**: bumped team.yi.semantic-gitlog version from 0.5.12 to 0.5.13 ([8be2eb39](https://github.com/ymind/rsql-querydsl/commit/8be2eb394c726a643847a30c8534a8ea64a4fa54))
- **gradle**: bumped org.jlleitschuh.gradle.ktlint version from 9.3.0 to 9.4.0 ([dc3019d4](https://github.com/ymind/rsql-querydsl/commit/dc3019d4f07c5fc8ef6ac948d80ca8caad355fd1))


### Tests

- print sql and parameters ([0ede76b7](https://github.com/ymind/rsql-querydsl/commit/0ede76b797b702be338c2bdee41e6f0eeddf2226))


### Styles

- add ktlint plugin and fix code styles ([21f6a89b](https://github.com/ymind/rsql-querydsl/commit/21f6a89bd7f557217c0da854b4f7b6c37ef9058f))
- adjust code styles ([1cae9169](https://github.com/ymind/rsql-querydsl/commit/1cae9169dfec2cba57c46d795572d952954e2cdf))
- code cleanup ([c2fde624](https://github.com/ymind/rsql-querydsl/commit/c2fde624c007ebcec7e428dec27cd467597070a3))


### Documentation

- **changelog**: adjust changelog templates ([e2a191d6](https://github.com/ymind/rsql-querydsl/commit/e2a191d66ae8a183f9eb193a98d0f0afcb92eb44))
- update docs ([251e3f7f](https://github.com/ymind/rsql-querydsl/commit/251e3f7fd4680ffd86885ad7117873af25c06d6c))


### Build System

- **chore**: bumped querydsl version from 4.3.1 to 4.4.0 ([7bb0af4f](https://github.com/ymind/rsql-querydsl/commit/7bb0af4fca5c10322d86510daa2fa55a87fd84b8))
- **chore**: bumped jackson-module-kotlin version from 2.11.2 to 2.11.3 ([1972f7b3](https://github.com/ymind/rsql-querydsl/commit/1972f7b3b071867ff8eb24a9029fb704ed509a20))
- **gradle**: bumped gradle wrapper version from 6.4.1 to 6.5.1 ([da2ca454](https://github.com/ymind/rsql-querydsl/commit/da2ca45418337e1329e8c6e5a6c4b82856eba68a))
- **gradle**: bumped gradle wrapper version from 6.5.1 to 6.6.1 ([19ed075c](https://github.com/ymind/rsql-querydsl/commit/19ed075c519e9ce957892ddde5ebc690b8bec3f5))
- **gradle**: bumped semantic-gitlog version from 0.5.13 to 0.5.17 ([a666c175](https://github.com/ymind/rsql-querydsl/commit/a666c175d2eb26151e5fdc6d3011ec9788ba241c))
- **gradle**: bumped ktlint version from 9.4.0 to 9.4.1 ([136c0925](https://github.com/ymind/rsql-querydsl/commit/136c092548a2a6def6304de741f28937d043c9e2))
- **kotlin**: bumped kotlin version from 1.3.72 to 1.4.0 ([727330e9](https://github.com/ymind/rsql-querydsl/commit/727330e9964bf1efb96f35219ef6012246997f91))
- **kotlin**: bumped kotlin version from 1.4.0 to 1.4.10 ([0aeb5d0c](https://github.com/ymind/rsql-querydsl/commit/0aeb5d0c970ba9b3924d8467904bcb897d5a5877))


### Continuous Integration

- **github**: disable push-back ([552c8f10](https://github.com/ymind/rsql-querydsl/commit/552c8f10cd58c4e3a00e3f30be3ea2d29ac4de4b))
- **github**: adjust ci config ([0f06f6cc](https://github.com/ymind/rsql-querydsl/commit/0f06f6cc56b273b0d07ae89510f4f175e85a2582))
- **github**: adjust project version update command ([4c7f68e9](https://github.com/ymind/rsql-querydsl/commit/4c7f68e97fcded9d17ccb732f556a29309f66b56))


## 0.1.0 (2020-06-03)

### Features

- 实现主要功能 ([d3336750](https://github.com/ymind/rsql-querydsl/commit/d333675068fbd3051b8a6fd06b6e34d8826f73bd))

