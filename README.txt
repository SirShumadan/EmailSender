1.  resources\message.txt - исходный текст письма.
    Для подстановки имени и номера телефона используются шаблоны:
    {name} - ФИО получателя
    {number} - номер телефона

2.  Программа запускается с двумя аргументами:
        -args[0] - Path config.properties
        -args[1] - Path message.txt

        Для запуска через console:
        mvn compile
        mvn exec:java -Dexec.args="<config.properties path> <message.txt path>"