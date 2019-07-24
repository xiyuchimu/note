--1����֪Ա����ÿ������Ϊ��н�ʣ���Ч*0.8�������ЧΪnull�����ʾ��ЧΪ 0����ѯԱ���������Լ������루����Ϊmoney�����������������������С�
select first_name,
       last_name,
       salary + nvl(commission_pct, 0) * 0.8 as "money"
  from employees
 order by "money" asc;
--2����ѯ��������������Ա������͹��ʣ�������͹��ʲ��ܵ���800����û�й����ߵ�Ա�����������ڡ�
select manager_id, min(salary)
  from employees
 group by manager_id
having manager_id is not null and min(salary) > 2200
 order by min(salary);
--3����ѯ�����ŵ�ƽ����Ч�������ЧΪnull������ֵ0����ͳ��
select department_id, avg(nvl(commission_pct, 0))
  from employees
 group by department_id
 --4����ѯ���в��ŵ����ơ����ڵء�Ա�������Լ�ƽ������
select d.department_id, d.department_name, l.city, count(1), avg(salary)
  from employees e, departments d, locations l
 where e.department_id = d.department_id
   and d.location_id = l.location_id
 group by d.department_id, d.department_name, l.city
--5����ѯԱ���ı�š����������ű��롢���������Լ��������ڳ��С�Ҫ�󣺰�û��Ա���Ĳ���Ҳ�������
select e.employee_id,
       e.first_name,
       e.last_name,
       e.department_id,
       d.department_name,
       l.city
  from employees e
  left join departments d
    on e.department_id = d.department_id
  left join locations l
    on d.location_id = l.location_id
--1)дSQL��䣬��ѯ�ĸ����ŵ�ƽ����������ߵģ��г����ű��롢ƽ������
select * from (select e.employee_id, avg(salary)
          from employees e
         group by e.employee_id order by avg(salary) desc) where rownum=1
--2)дSQL��䣬�г����������й�����ߵ�Ա������Ϣ�����֡����źš����ʡ�

