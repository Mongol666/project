#<span style="color:greenyellow">***На данном этапе сделано:***</span>

* <span style="color:gold">*Полный перенос на Hibernate*</span>
****
* <span style="color:gold">*Удалены старые репозитории для нормальной работы версии Hibernate*</span>
****
* <span style="color:gold">*Исправлены "каскады" для таблиц, имеющих foreign key*</span>
****
* <span style="color:gold">*Общие методы (```create```, ```getByID```, ```update```, ```remove```)
  вынесены в абстрактный класс ```AbstractRepoJpa.java```*</span>


###<span style="color:greenyellow">***Примечания:***</span>

<span style="color:color:gold">*Исправлено два предыдущих бага: удаление студента и предмета из группы работает через NativeSQL, изменены "каскадные типы"*</span>


