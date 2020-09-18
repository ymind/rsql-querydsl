# Changelog

## 0.2.4 (2020-09-18)

### Code Refactoring

- rename `RsqlConfig.getFieldTypeHandlers` to `RsqlConfig.addFieldTypeHandlers` ([7bd52a4c](https://github.com/ymind/rsql-querydsl/commit/7bd52a4c4f5314ec5ef80ccc038702ffbb229363))
- remove name field from RsqlOperator ([0d8f36ae](https://github.com/ymind/rsql-querydsl/commit/0d8f36ae77a1e185f6297123984e9fc8a3fa6582))
- typo fix and code cleanup ([126d59e7](https://github.com/ymind/rsql-querydsl/commit/126d59e7dabb1ff32b08e1bfa31a979f17fe8612))
- split SortFieldTypeHandler ([2f64a51c](https://github.com/ymind/rsql-querydsl/commit/2f64a51c0c5646f311f6a137d3bc613bc4caf1f8))
- optimize type handlers ([c01d6e4e](https://github.com/ymind/rsql-querydsl/commit/c01d6e4e1b3a21ab3030d63b9b0955b76ddd10ea))
- make EntityManager none null ([7ea867a4](https://github.com/ymind/rsql-querydsl/commit/7ea867a4a49327f7341e19ace69742a9aec45175))
- make regexOptions private ([762b8339](https://github.com/ymind/rsql-querydsl/commit/762b83399b40e30ba8b8c7b3aaebd9f6d023347c))


### Features

- support custom entity field type handler ([ce1ee662](https://github.com/ymind/rsql-querydsl/commit/ce1ee662c714e068af3b8a52a0f0e4c731d46fd6))


### Performance Improvements

- **util**: enhance DateUtil ([c3f44b19](https://github.com/ymind/rsql-querydsl/commit/c3f44b1979daccce6acc057dbe3b283122ed454c))


### Styles

- code cleanup ([2d168474](https://github.com/ymind/rsql-querydsl/commit/2d168474ed319c04de6831c43a131112e87c3448))


### Chores

- **deps**: upgrade org.jlleitschuh.gradle.ktlint version from 9.3.0 to 9.4.0 ([e7d7f497](https://github.com/ymind/rsql-querydsl/commit/e7d7f497b1e8a0e0283f1717343787f62351690a))
- **deps**: upgrade spring version from 2.3.2.RELEASE to 2.3.3.RELEASE ([5deb9de1](https://github.com/ymind/rsql-querydsl/commit/5deb9de17eb5d3896d306c942c1f9982aa12d08d))
- **deps**: upgrade team.yi.semantic-gitlog version from 0.5.12 to 0.5.13 ([1b0bb49c](https://github.com/ymind/rsql-querydsl/commit/1b0bb49c02428a1850f7edf5ffa6525d8ab4cf24))
- **deps**: upgrade spring version from 2.3.3.RELEASE to 2.3.4.RELEASE ([f437a348](https://github.com/ymind/rsql-querydsl/commit/f437a34861970107a3ef251f465e774bb012bf00))


### Build System

- **gradle**: upgrade gradle version from 6.5.1 to 6.6.1 ([71eccb2f](https://github.com/ymind/rsql-querydsl/commit/71eccb2f3f91822bb1f853a690ef6f82717b700a))
- **kotlin**: upgrade kotlin version from 1.3.72 to 1.4.0 ([9b3d1692](https://github.com/ymind/rsql-querydsl/commit/9b3d1692fecdfeaae041ae7a3cc1c1014c6aa947))
- **kotlin**: upgrade kotlin version from 1.4.0 to 1.4.10 ([8efdbb95](https://github.com/ymind/rsql-querydsl/commit/8efdbb951464e668074d36d872242ef2620c481a))


## 0.1.10 (2020-08-18)

### Bug Fixes

- **common**: fix `FieldNotSupportedException` arguments type ([79305a96](https://github.com/ymind/rsql-querydsl/commit/79305a960f69425cad91dc583ea257fd63b1cfae))


### Styles

- add ktlint plugin and fix code styles ([86cbdc7f](https://github.com/ymind/rsql-querydsl/commit/86cbdc7fff7d12af278b569fed3d1140aa7a535c))


### Tests

- print sql and parameters ([2e536968](https://github.com/ymind/rsql-querydsl/commit/2e536968f1cddbca14ecca7561d15da4568872be))


## 0.1.9 (2020-08-05)

### Bug Fixes

- QuerydslRsql.buildPredicate() return null when globalPredicate is null ([a335828e](https://github.com/ymind/rsql-querydsl/commit/a335828e0f1c2b9dfaa4101dc03a96ff10aa92d4))


### Documentation

- **changelog**: update changelog templates ([2763ff33](https://github.com/ymind/rsql-querydsl/commit/2763ff33d0c2a5724a76d2790fcbc026526c62bd))


### Styles

- adjust code styles ([22bd64c3](https://github.com/ymind/rsql-querydsl/commit/22bd64c3221f82cdbb96aaf3291bc33714595fa5))


### Chores

- **deps**: bump team.yi.semantic-gitlog from 0.5.3 to 0.5.12 ([36bf5e01](https://github.com/ymind/rsql-querydsl/commit/36bf5e01191d3691c0c73c4fc0bdfea6144469e8))
- **deps**: remove versions plugins ([ad710241](https://github.com/ymind/rsql-querydsl/commit/ad7102414c2e1a845b9dda95d457c0eb7668aa70))
- **deps**: bump spring boot from 2.3.1.RELEASE to 2.3.2.RELEASE ([6a36bc01](https://github.com/ymind/rsql-querydsl/commit/6a36bc01b6dfb7ce9333ee756507c2c044ad17c8))
- **deps**: bump jackson-module-kotlin from 2.11.1 to 2.11.2 ([c077a25d](https://github.com/ymind/rsql-querydsl/commit/c077a25d2d035ec4156f0d372e2533cc96c65df4))
- **deps**: bump commons-lang3 from 3.10 to 3.11 ([f1548e01](https://github.com/ymind/rsql-querydsl/commit/f1548e01b77ff988cc0a8f76851afa65e50a71aa))


### Continuous Integration

- **github**: adjust ci config ([b056c2e0](https://github.com/ymind/rsql-querydsl/commit/b056c2e0e60da73d749e03f82043173915ee91cf))


## 0.1.2 (2020-08-02)

### Chores

- **deps**: update versions ([c20d70c8](https://github.com/ymind/rsql-querydsl/commit/c20d70c8447c13c055f80bdb950816c533855cfc))


### Build System

- **gradle**: upgrade gradle wrapper to 6.5.1 ([e7cb6c17](https://github.com/ymind/rsql-querydsl/commit/e7cb6c17d89e77067610fbe8216d21b886b99928))


### Continuous Integration

- **github**: disable push-back ([72bab6a1](https://github.com/ymind/rsql-querydsl/commit/72bab6a185119ea11cc8c508f0d20b99d4a2d6a5))


## 0.1.0 (2020-06-03)

### Features

- implement primary features and challenges ([b8132d53](https://github.com/ymind/rsql-querydsl/commit/b8132d53c66694f519bc5104d3843f934a008200))

