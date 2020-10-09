![rsql-querydsl](https://github.com/ymind/rsql-querydsl/workflows/rsql-querydsl/badge.svg?branch=master)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/ymind/rsql-querydsl)](https://github.com/ymind/rsql-querydsl/releases)
[![Maven Central](https://img.shields.io/maven-central/v/team.yi.rsql/rsql-querydsl)](https://search.maven.org/artifact/team.yi.rsql/rsql-querydsl)
[![Semantic Versioning 2.0.0](https://img.shields.io/badge/Semantic%20Versioning-2.0.0-brightgreen)](https://semver.org/)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://conventionalcommits.org)
[![GitHub](https://img.shields.io/github/license/ymind/rsql-querydsl)](https://github.com/ymind/rsql-querydsl/blob/master/LICENSE)

# rsql-querydsl

Integration RSQL query language and Querydsl framework.

# Quick start

```kotlin
val rsql = QuerydslRsql.Builder<Car>(entityManager)
    .from("Car")
    .where("active=isnull=1")
    .build()
val cars = rsql.fetch()

// will generate SQL:
//    select car0_.id          as id1_0_,
//           car0_.active      as active2_0_,
//           car0_.description as descript3_0_,
//           car0_.engine_id   as engine_i6_0_,
//           car0_.mfgdt       as mfgdt4_0_,
//           car0_.name        as name5_0_
//    from car car0_
//    where car0_.active is null
```

For more usage, please refer to https://ymind.github.io/rsql-querydsl

# Author

[@ymind][6], full stack engineer.

# License

This is open-sourced software licensed under the [MIT license][9].

[6]: https://github.com/ymind
[9]: https://opensource.org/licenses/MIT
