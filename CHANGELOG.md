# Changelog

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

- implement primary features and challenges ([d3336750](https://github.com/ymind/rsql-querydsl/commit/d333675068fbd3051b8a6fd06b6e34d8826f73bd))

