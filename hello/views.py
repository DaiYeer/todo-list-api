from django.shortcuts import render

import json

from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt

from .models import Tarea


@csrf_exempt
def tareas(request):

    
    if request.method == 'GET':

        lista = Tarea.objects.all()

        datos = []

        for tarea in lista:
            datos.append({
                'id': tarea.id,
                'titulo': tarea.titulo,
                'descripcion': tarea.descripcion,
                'completada': tarea.completada
            })

        return JsonResponse(datos, safe=False)


    elif request.method == 'POST':

        try:
            datos = json.loads(request.body)

            tarea = Tarea.objects.create(
                titulo=datos['titulo'],
                descripcion=datos['descripcion'],
                completada=datos.get('completada', False)
            )

            return JsonResponse({
                'mensaje': 'Tarea creada correctamente',
                'id': tarea.id
            }, status=201)

        except (ValueError, KeyError, TypeError):
            return JsonResponse({
                'error': 'Datos incorrectos'
            }, status=400)


@csrf_exempt
def tarea_detalle(request, id):

    try:
        tarea = Tarea.objects.get(id=id)

    except Tarea.DoesNotExist:
        return JsonResponse({
            'error': 'Tarea no encontrada'
        }, status=404)


   
    if request.method == 'GET':

        datos = {
            'id': tarea.id,
            'titulo': tarea.titulo,
            'descripcion': tarea.descripcion,
            'completada': tarea.completada
        }

        return JsonResponse(datos)


    
    elif request.method == 'PUT':

        try:
            datos = json.loads(request.body)

            tarea.titulo = datos['titulo']
            tarea.descripcion = datos['descripcion']
            tarea.completada = datos.get('completada', tarea.completada)

            tarea.save()

            return JsonResponse({
                'mensaje': 'Tarea actualizada correctamente'
            })

        except (ValueError, KeyError, TypeError):
            return JsonResponse({
                'error': 'Datos incorrectos'
            }, status=400)


    
    elif request.method == 'DELETE':

        tarea.delete()

        return JsonResponse({
            'mensaje': 'Tarea eliminada correctamente'
        })


    return JsonResponse({
        'error': 'Metodo no permitido'
    }, status=405)
