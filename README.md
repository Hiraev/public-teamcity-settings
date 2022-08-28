1. Переходим в deploy, собираем и поднимает сервер вместе с агентом
```docker
docker compose up --build -d
```

2. В Веб-интерфейсе в агентах видим неавторизованного агента. Авторизовываем его

3. Генерируем у себя ssh ключ.  (passprhase ключевое слово для доступа к приватному ключу)
```bash
ssh-keygen -f teamcity_rsa -N passphrase -t rsa -m PEM
```
4. Заходим в Administration -> Projects -> Root Project -> SSH keys И добавляем сюда teamcity_rsa файл, который мы сгенерировали
5. В github добавляем ssh ключ скопированный из teamcity_rsa.pub
6. Создаем пустой проект на github и копируем ссылку на него в формате https://github.com/Hiraev/public-teamcity-settings.git
7. Открывает в Teamcity Administration -> Projects -> Root Project -> VCS Roots -> Create VCS Roots
В repository url указываем скопированный на шаге 10 url и жмем Create. (В проекте должная быть хотя бы одна ветка). При подключении будет использоваться ключ, который мы добавили на шаге 7-9.
8. Открываем Administration -> Projects -> Root Project -> Versioned Settings. Выбираем Synchronization enabled. В списке выбираем VCS, который добавили в пункте 11. Settings Format выбираем "Kotlin". Галочка "Generate Portable DSL Scripts" включена. Жмем "Apply". Затем жмем "Commit current project settings". Если на наш сервер на github ничего не запушилось, то идем в Administration -> Projects -> Root Project, жмем справа "Actions" выбираем "Download Settings in Kotlin format" (Generate portable DSL scripts включено). Скачиваем настройки.
9. Все файлы перекидываем в наш проект на github в папку .teamcity и пушим на сервер
10. В Administration -> Projects -> Root Project -> Versioned Settings жмем "Load Project Settings from VCS"
11. Открываем Intellij IDEA. Открываем в нем git проект в котором лежит .teamcity. Intellij Idea предложит импортировать проект .teamcity (В правом нижнем углу), соглашаемся. Переходим в .teamcity выполняем команду  mvn teamcity-configs:generate (Зачем это делать пока не знаю). При этом смотрим чтобы урл по которому запрашиваются либы, был таким же, какой и в pom.xml для teamcity-server. !!! По умолчанию maven блокирует http запросы. Можно увидеть в логах `maven-default-http-blocker` и использование ip: 0.0.0.0. Если вы уверены, то можно добавить url в исключения.
Для этого нужно изменить/добавить файл ~/.m2/settings.xml.
```xml
<settings>
    <mirrors>
        <mirror>
            <id>release-http-unblocker</id>
            <mirrorOf>teamcity-server</mirrorOf>
            <name></name>
            <url>http://192.168.1.190:8111/app/dsl-plugins-repository</url>
        </mirror>
    </mirrors>
</settings>
```
Для репозитория из pom.xml
```xml
    <repository>
      <id>teamcity-server</id>
      <url>http://192.168.1.190:8111/app/dsl-plugins-repository</url>
      <snapshots>
        <enabled>true</enabled>
      </snapshots>
    </repository>
```
