--1）已知员工的每月收入为：薪资＋绩效*0.8，如果绩效为null，则表示绩效为 0。查询员工的姓名以及月收入（列名为money），并按照月收入升序排列。
select first_name,
       last_name,
       salary + nvl(commission_pct, 0) * 0.8 as "money"
  from employees
 order by "money" asc;
--2）查询各个管理者属下员工的最低工资，其中最低工资不能低于800，且没有管理者的员工不计算在内。
select manager_id, min(salary)
  from employees
 group by manager_id
having manager_id is not null and min(salary) > 2200
 order by min(salary);
--3）查询各部门的平均绩效，如果绩效为null，则按数值0进行统计
select department_id, avg(nvl(commission_pct, 0))
  from employees
 group by department_id
 --4）查询所有部门的名称、所在地、员工数量以及平均工资
select d.department_id, d.department_name, l.city, count(1), avg(salary)
  from employees e, departments d, locations l
 where e.department_id = d.department_id
   and d.location_id = l.location_id
 group by d.department_id, d.department_name, l.city
--5）查询员工的编号、姓名、部门编码、部门名称以及部门所在城市。要求：把没有员工的部门也查出来。
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
--1)写SQL语句，查询哪个部门的平均工资是最高的，列出部门编码、平均工资
select * from (select e.employee_id, avg(salary)
          from employees e
         group by e.employee_id order by avg(salary) desc) where rownum=1
--2)写SQL语句，列出各个部门中工资最高的员工的信息：名字、部门号、工资。

