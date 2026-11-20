from django.urls import path
from . import views

urlpatterns = [
    path('tareas/', views.tareas),
    path('tareas/<int:id>/', views.tarea_detalle),
]