Currency Exchange App for IProgrammers

El aplicativo fue creado con la versión de Eclipse IDE for Enterprise Java Developers. https://www.eclipse.org/downloads/ Eclipse IDE 2019‑03 64 bits. Instale la opción "Eclipse IDE for Enterprise Java Developers".

Al ser un aplicativo web utilice el apache-tomcat-8.5.39 64 bits: https://tomcat.apache.org/download-80.cgi

Para evitar utilizar una base de datos, y así, simplificar la instalación del aplicativo, el historico de las consultas se guarda en un archivo de texto (HistoryExchange.txt).

Como se recomendo en el instructivo uilice el servicio REST de cambio.today.La key que la página te otorga para utilizar el servicio REST se almaceno en el archivo config.properties, por si fuera necesario modificar la clave.

El proyecto se creo con Maven, por lo tanto no debería tener que agregar ninguna dependencia. Sólo por si las dudas las dependencias que utilice son: org.springframework.boot (web y thymeleaf), org.json y org.springframework.data (commons). También se puede revisar en el archivo pom.xml.

Por último para correr la aplicación debe darse click derecho en el archivo main.java (path: src\main\java\com\iprogrammers\app), 
Run As -> java Application. Si todo salio bien se vera en la consola el logo de Spirng y al final del log la leyenda "Tomcat started on port(s): 8080 (http)", entonces el tomcat ya esta corriendo y puede abrir la página utilizando localhost:[puerto configurado en tomcat]. 

Todo está en la página principal, un dropdown para cada moneda a elegir y un campo númerico para la entrada de la cantidad, utilizando el botón convert devuelve el resultado utilizando el servicio REST de cambio.today. En la misma página principal se puede acceder a través del link "Show History" al historico de consultas realizadas ordenado de la última consulta a la primera que se realizó.

Si es necesaría mi asistencia para lograr correr el aplicativo web me pueden contactar vía mail o por celular.
