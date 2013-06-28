nanorm is a simple and lightweight data mapper framework inspired by the [http://ibatis.apache.org Apache iBatis]. It is not meant to be an full ORM solution.

The main features are:

 * Strict typing where possible
 * Easy to use
 * Easy to mock in unit tests
 * Low performance overhead compared to hand-written JDBC mapper

Sample code
-----------

Declare an interface:

    public interface CarMapper {
        /**
         * Get car by id.
         * Declare a mapping. In this case it is automatic, meaning that properties
         * not explicitly mapped will be mapped to the columns with same name.
         * Explicitly map owner column to ownerName property.
         */
        @ResultMap(id = "car", auto = true, mappings = {     
            @Mapping(value = "ownerName", column = "owner")
        })
        @Select("SELECT id, model, owner, year FROM cars WHERE ID = ${1}")
        Car getCarById(int id);
    }

Car is a POJO with _id_, _model_, _year_ and _ownerName_ properties.

Using the mapper:

    NanormFactory factory = ... // Create factory
    Connection conn = ... // JDBC connection
    CarMapper mapper = factory.createMapper(CarMapper.class);

    // Make factory to use this connection for current thread
    Session sess = factory.openSession(conn);
    try {
      Car car = mapper.getCarById(1);
      // Use it
      ...
      sess.commit();
    } finally {
      sess.end();
    }
