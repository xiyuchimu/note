1.SpringData概述
//spring的一个子项目。用于简化数据库访问，支持NoSql和关系数据看
//MongoDB、Redis，Hbase
//关系数据访问技术：jdbc，jpa
  jpa Spring data概述
//致力于减少数据访问层（DAO）的开发量，开发者唯要做的，就是声明持久层的接口，其它
  都交给Spring data jpa
2.Spring data helloWord
 //syso（datasource.getConnetction)
3.--repository是一个空接口，即一个标记接口
  //若我们定义的接口继承了repository，则该接口会被ioc容器识别为一个repository bean 纳入到ioc容器中，
    进而可以在该接口中定义满足一定规范的方法
  //实际上也可以通过@RepositoryDefinition代替继承Repository接口
  //crudRepository
  //PagingAndSortingRepository   
  //jpaRepository
  //jpaSpeclFicationExecutor
4.spring Data Repository查询方法定义规范
  --
5.query注解
    @Query("SELECT p FROM Person p WHERE p.id = (SELECT max(p2.id) FROM Person p2)")
	Person getMaxIdPerson();
	
	//为 @Query 注解传递参数的方式1: 使用占位符. 
	@Query("SELECT p FROM Person p WHERE p.lastName = ?1 AND p.email = ?2")
	List<Person> testQueryAnnotationParams1(String lastName, String email);
	
	//为 @Query 注解传递参数的方式1: 命名参数的方式. 
	@Query("SELECT p FROM Person p WHERE p.lastName = :lastName AND p.email = :email")
	List<Person> testQueryAnnotationParams2(@Param("email") String email, @Param("lastName") String lastName);
	
	//SpringData 允许在占位符上添加 %%. 
	@Query("SELECT p FROM Person p WHERE p.lastName LIKE %?1% OR p.email LIKE %?2%")
	List<Person> testQueryAnnotationLikeParam(String lastName, String email);
	
	//SpringData 允许在占位符上添加 %%. 
	@Query("SELECT p FROM Person p WHERE p.lastName LIKE %:lastName% OR p.email LIKE %:email%")
	List<Person> testQueryAnnotationLikeParam2(@Param("email") String email, @Param("lastName") String lastName);
	
	//设置 nativeQuery=true 即可以使用原生的 SQL 查询
	@Query(value="SELECT count(id) FROM jpa_persons", nativeQuery=true)
	long getTotalCount();
6.Modifying注解
    //可以通过自定义的 JPQL 完成 UPDATE 和 DELETE 操作. 注意: JPQL 不支持使用 INSERT
	//在 @Query 注解中编写 JPQL 语句, 但必须使用 @Modifying 进行修饰. 以通知 SpringData, 这是一个 UPDATE 或 DELETE 操作
	//UPDATE 或 DELETE 操作需要使用事务, 此时需要定义 Service 层. 在 Service 层的方法上添加事务操作. 
	//默认情况下, SpringData 的每个方法上有事务, 但都是一个只读事务. 他们不能完成修改操作!
    @Modifying
	@Query("UPDATE Person p SET p.email = :email WHERE id = :id")
	void updatePersonEmail(@Param("id") Integer id, @Param("email") String email);
7.curdRepository接口
    @Test
	public void testCrudReposiory(){
		List<Person> persons = new ArrayList<>();		
		for(int i = 'a'; i <= 'z'; i++){
			Person person = new Person();
			person.setAddressId(i + 1);
			person.setBirth(new Date());
			person.setEmail((char)i + "" + (char)i + "@atguigu.com");
			person.setLastName((char)i + "" + (char)i);			
			persons.add(person);
		}
		
		personService.savePersons(persons);
	}
8.pagingAndSortingRespository接口
    @Test
	public void testPagingAndSortingRespository(){
		//pageNo 从 0 开始. 
		int pageNo = 6 - 1;
		int pageSize = 5;
		//Pageable 接口通常使用的其 PageRequest 实现类. 其中封装了需要分页的信息
		//排序相关的. Sort 封装了排序的信息
		//Order 是具体针对于某一个属性进行升序还是降序. 
		Order order1 = new Order(Direction.DESC, "id");
		Order order2 = new Order(Direction.ASC, "email");
		Sort sort = new Sort(order1, order2);
		
		PageRequest pageable = new PageRequest(pageNo, pageSize, sort);
		Page<Person> page = personRepsotory.findAll(pageable);
		
		System.out.println("总记录数: " + page.getTotalElements());
		System.out.println("当前第几页: " + (page.getNumber() + 1));
		System.out.println("总页数: " + page.getTotalPages());
		System.out.println("当前页面的 List: " + page.getContent());
		System.out.println("当前页面的记录数: " + page.getNumberOfElements());
	}
9.jpaRepository接口
	@Test
	public void testJpaRepository(){
		Person person = new Person();
		person.setBirth(new Date());
		person.setEmail("xy@atguigu.com");
		person.setLastName("xyz");
		person.setId(28);		
		Person person2 = personRepsotory.saveAndFlush(person);		
		System.out.println(person == person2);
	}
10.jpaSpecificationExecutor接口
    @Test
	public void testJpaSpecificationExecutor(){
		int pageNo = 3 - 1;
		int pageSize = 5;
		PageRequest pageable = new PageRequest(pageNo, pageSize);
		
		//通常使用 Specification 的匿名内部类
		Specification<Person> specification = new Specification<Person>() {
			/**
			 * @param *root: 代表查询的实体类. 
			 * @param query: 可以从中可到 Root 对象, 即告知 JPA Criteria 查询要查询哪一个实体类. 还可以
			 * 来添加查询条件, 还可以结合 EntityManager 对象得到最终查询的 TypedQuery 对象. 
			 * @param *cb: CriteriaBuilder 对象. 用于创建 Criteria 相关对象的工厂. 当然可以从中获取到 Predicate 对象
			 * @return: *Predicate 类型, 代表一个查询条件. 
			 */
			@Override
			public Predicate toPredicate(Root<Person> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				Path path = root.get("id");
				Predicate predicate = cb.gt(path, 5);
				return predicate;
			}
		};
		
		Page<Person> page = personRepsotory.findAll(specification, pageable);
		
		System.out.println("总记录数: " + page.getTotalElements());
		System.out.println("当前第几页: " + (page.getNumber() + 1));
		System.out.println("总页数: " + page.getTotalPages());
		System.out.println("当前页面的 List: " + page.getContent());
		System.out.println("当前页面的记录数: " + page.getNumberOfElements());
	}
11.自定义Repository方法
   //1.
   public interface PersonDao {	
	  void test();
	}
   //2.
   public class PersonRepsotoryImpl implements PersonDao {	
	    @PersistenceContext
	    private EntityManager entityManager;
	
	    @Override
	    public void test() {
		    Person person = entityManager.find(Person.class, 11);
		    System.out.println("-->" + person);
	    }
   }
   //3.
   public interface PersonRepsotory extends  PersonDao



/**
注：
  Repository（空接口）
    |--CurdRepository
	  |--PagingAndSortingRepository
	    |--JpaRepository
  JpaSpecificationExecutor
*/