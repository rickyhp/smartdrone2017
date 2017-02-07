from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^postagger/', views.postagger, name='postagger_english'),
    url(r'^similarity/', views.similarity, name='similarity_english'),
]
