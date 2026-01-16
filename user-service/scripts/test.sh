#!/bin/bash

echo "Тестирование Notification Service"
echo "===================================="

# 1. Проверка здоровья
echo "1. Проверка здоровья сервиса:"
curl -s http://localhost:8081/api/health
echo ""
echo ""

# 2. Отправка тестового email
echo "2. Отправка тестового email:"
curl -X POST "http://localhost:8081/api/send-email?to=test@example.com&subject=Test Subject&text=This is a test email"
echo ""
echo ""

# 3. Отправка приветственного письма
echo "3. Отправка приветственного письма:"
curl -X POST "http://localhost:8081/api/welcome?email=welcome@example.com&name=Иван"
echo ""
echo ""

# 4. Отправка письма об удалении
echo "4. Отправка письма об удалении:"
curl -X POST "http://localhost:8081/api/goodbye?email=goodbye@example.com&name=Петр"
echo ""
echo ""

echo "Тестирование завершено!"
echo "Проверьте вывод в консоли notification-service"