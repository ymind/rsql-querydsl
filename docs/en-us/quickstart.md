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
