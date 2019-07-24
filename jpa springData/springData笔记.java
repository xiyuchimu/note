1.SpringData����
//spring��һ������Ŀ�����ڼ����ݿ���ʣ�֧��NoSql�͹�ϵ���ݿ�
//MongoDB��Redis��Hbase
//��ϵ���ݷ��ʼ�����jdbc��jpa
  jpa Spring data����
//�����ڼ������ݷ��ʲ㣨DAO���Ŀ�������������ΨҪ���ģ����������־ò�Ľӿڣ�����
  ������Spring data jpa
2.Spring data helloWord
 //syso��datasource.getConnetction)
3.--repository��һ���սӿڣ���һ����ǽӿ�
  //�����Ƕ���Ľӿڼ̳���repository����ýӿڻᱻioc����ʶ��Ϊһ��repository bean ���뵽ioc�����У�
    ���������ڸýӿ��ж�������һ���淶�ķ���
  //ʵ����Ҳ����ͨ��@RepositoryDefinition����̳�Repository�ӿ�
  //crudRepository
  //PagingAndSortingRepository   
  //jpaRepository
  //jpaSpeclFicationExecutor
4.spring Data Repository��ѯ��������淶
  --
5.queryע��
    @Query("SELECT p FROM Person p WHERE p.id = (SELECT max(p2.id) FROM Person p2)")
	Person getMaxIdPerson();
	
	//Ϊ @Query ע�⴫�ݲ����ķ�ʽ1: ʹ��ռλ��. 
	@Query("SELECT p FROM Person p WHERE p.lastName = ?1 AND p.email = ?2")
	List<Person> testQueryAnnotationParams1(String lastName, String email);
	
	//Ϊ @Query ע�⴫�ݲ����ķ�ʽ1: ���������ķ�ʽ. 
	@Query("SELECT p FROM Person p WHERE p.lastName = :lastName AND p.email = :email")
	List<Person> testQueryAnnotationParams2(@Param("email") String email, @Param("lastName") String lastName);
	
	//SpringData ������ռλ������� %%. 
	@Query("SELECT p FROM Person p WHERE p.lastName LIKE %?1% OR p.email LIKE %?2%")
	List<Person> testQueryAnnotationLikeParam(String lastName, String email);
	
	//SpringData ������ռλ������� %%. 
	@Query("SELECT p FROM Person p WHERE p.lastName LIKE %:lastName% OR p.email LIKE %:email%")
	List<Person> testQueryAnnotationLikeParam2(@Param("email") String email, @Param("lastName") String lastName);
	
	//���� nativeQuery=true ������ʹ��ԭ���� SQL ��ѯ
	@Query(value="SELECT count(id) FROM jpa_persons", nativeQuery=true)
	long getTotalCount();
6.Modifyingע��
    //����ͨ���Զ���� JPQL ��� UPDATE �� DELETE ����. ע��: JPQL ��֧��ʹ�� INSERT
	//�� @Query ע���б�д JPQL ���, ������ʹ�� @Modifying ��������. ��֪ͨ SpringData, ����һ�� UPDATE �� DELETE ����
	//UPDATE �� DELETE ������Ҫʹ������, ��ʱ��Ҫ���� Service ��. �� Service ��ķ���������������. 
	//Ĭ�������, SpringData ��ÿ��������������, ������һ��ֻ������. ���ǲ�������޸Ĳ���!
    @Modifying
	@Query("UPDATE Person p SET p.email = :email WHERE id = :id")
	void updatePersonEmail(@Param("id") Integer id, @Param("email") String email);
7.curdRepository�ӿ�
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
8.pagingAndSortingRespository�ӿ�
    @Test
	public void testPagingAndSortingRespository(){
		//pageNo �� 0 ��ʼ. 
		int pageNo = 6 - 1;
		int pageSize = 5;
		//Pageable �ӿ�ͨ��ʹ�õ��� PageRequest ʵ����. ���з�װ����Ҫ��ҳ����Ϣ
		//������ص�. Sort ��װ���������Ϣ
		//Order �Ǿ��������ĳһ�����Խ��������ǽ���. 
		Order order1 = new Order(Direction.DESC, "id");
		Order order2 = new Order(Direction.ASC, "email");
		Sort sort = new Sort(order1, order2);
		
		PageRequest pageable = new PageRequest(pageNo, pageSize, sort);
		Page<Person> page = personRepsotory.findAll(pageable);
		
		System.out.println("�ܼ�¼��: " + page.getTotalElements());
		System.out.println("��ǰ�ڼ�ҳ: " + (page.getNumber() + 1));
		System.out.println("��ҳ��: " + page.getTotalPages());
		System.out.println("��ǰҳ��� List: " + page.getContent());
		System.out.println("��ǰҳ��ļ�¼��: " + page.getNumberOfElements());
	}
9.jpaRepository�ӿ�
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
10.jpaSpecificationExecutor�ӿ�
    @Test
	public void testJpaSpecificationExecutor(){
		int pageNo = 3 - 1;
		int pageSize = 5;
		PageRequest pageable = new PageRequest(pageNo, pageSize);
		
		//ͨ��ʹ�� Specification �������ڲ���
		Specification<Person> specification = new Specification<Person>() {
			/**
			 * @param *root: �����ѯ��ʵ����. 
			 * @param query: ���Դ��пɵ� Root ����, ����֪ JPA Criteria ��ѯҪ��ѯ��һ��ʵ����. ������
			 * ����Ӳ�ѯ����, �����Խ�� EntityManager ����õ����ղ�ѯ�� TypedQuery ����. 
			 * @param *cb: CriteriaBuilder ����. ���ڴ��� Criteria ��ض���Ĺ���. ��Ȼ���Դ��л�ȡ�� Predicate ����
			 * @return: *Predicate ����, ����һ����ѯ����. 
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
		
		System.out.println("�ܼ�¼��: " + page.getTotalElements());
		System.out.println("��ǰ�ڼ�ҳ: " + (page.getNumber() + 1));
		System.out.println("��ҳ��: " + page.getTotalPages());
		System.out.println("��ǰҳ��� List: " + page.getContent());
		System.out.println("��ǰҳ��ļ�¼��: " + page.getNumberOfElements());
	}
11.�Զ���Repository����
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
ע��
  Repository���սӿڣ�
    |--CurdRepository
	  |--PagingAndSortingRepository
	    |--JpaRepository
  JpaSpecificationExecutor
*/