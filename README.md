![rsql-querydsl](https://github.com/ymind/rsql-querydsl/workflows/rsql-querydsl/badge.svg?branch=master)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/ymind/rsql-querydsl)](https://github.com/ymind/rsql-querydsl/releases)
[![Maven Central](https://img.shields.io/maven-central/v/team.yi.rsql/rsql-querydsl)](https://search.maven.org/artifact/team.yi.rsql/rsql-querydsl)
[![Semantic Versioning 2.0.0](https://img.shields.io/badge/Semantic%20Versioning-2.0.0-brightgreen)](https://semver.org/)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://conventionalcommits.org)
[![GitHub](https://img.shields.io/github/license/ymind/rsql-querydsl)](https://github.com/ymind/rsql-querydsl/blob/master/LICENSE)

# rsql-querydsl

Integration RSQL query language and Querydsl framework.

> __WARNING: plans to drop support for java and use kotlin entirely.__

# Quick start

```kotlin
@Test
fun shouldReturnTupleWithLimitNumber() {
    val clazz = Car::class.java
    val pathBuilder = pathFactory.create(clazz)
    val selectFields = RsqlUtil.parseSelect("name,description", pathBuilder).toTypedArray()
    val select = Projections.bean(
        clazz,
        pathBuilder.getNumber("id", Long::class.java).add(1000).`as`("id"),
        *selectFields,
    )
    val rsqlConfig = RsqlConfig.Builder(entityManager).build()
    val querydslRsql = QuerydslRsql.Builder<Car>(rsqlConfig)
        .select(select)
        // .from(clazz)
        .where("id=notnull='' and (name=like='%a%' or name=con='Béla2,Béla11')")
        .sort("id.desc")
        .limit(15L, 15)
        .build()
    val cars = querydslRsql.buildJPAQuery(pathBuilder).fetch()

    assertFalse(cars.isEmpty(), "Can't handle limit expression")
    assertEquals(15, cars.size, "Can't handle limit expression")
}

// will generate SQL:
//     select car0_.id+? as col_0_0_, 
//            car0_.name as col_1_0_, 
//            car0_.description as col_2_0_ 
//     from car car0_ 
//     where (car0_.id is not null) and (car0_.name like ? escape '!' or car0_.name like ? escape '!') 
//     order by car0_.id desc 
//     limit ? 
//     offset ?
//
//     binding parameter [1] as [BIGINT] - [1000]
//     binding parameter [2] as [VARCHAR] - [%a%]
//     binding parameter [3] as [VARCHAR] - [%Béla2,Béla11%]
```

For more usage, please refer to https://ymind.github.io/rsql-querydsl

# Author

[@ymind][6], full stack engineer.

# License

This is open-sourced software licensed under the [MIT license][9].

[6]: https://github.com/ymind

[9]: https://opensource.org/licenses/MIT
