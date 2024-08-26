from http import HTTPStatus
from django.contrib.auth import authenticate, login, logout
from django.http import JsonResponse
from django.utils import timezone
from rest_framework import generics
from rest_framework.pagination import PageNumberPagination
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView
from .models import Event
from .serializers import EventSerializer

from rest_framework.permissions import BasePermission



class EventPagination(PageNumberPagination):
    page_size = 2
    page_size_query_param = 'page_size'
    max_page_size = 10

class EventListCreateView(generics.ListCreateAPIView):
    queryset = Event.objects.all()
    serializer_class = EventSerializer
    pagination_class = EventPagination
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        now = timezone.now()
        queryset = Event.objects.filter(event_datetime__gte=now).order_by('event_datetime')
    
        start_date = self.request.query_params.get('start_date', None)
        end_date = self.request.query_params.get('end_date', None)
        
        if start_date:
            queryset = queryset.filter(event_datetime__gte=start_date)
        if end_date:
            queryset = queryset.filter(event_datetime__lte=end_date)
        
        return queryset

    def perform_create(self, serializer):
        serializer.save(created_by=self.request.user)

    

class EventRetrieveUpdateDestroyView(generics.RetrieveUpdateDestroyAPIView):
    queryset = Event.objects.all()
    serializer_class = EventSerializer
    permission_classes = [IsAuthenticated]

import logging

logger = logging.getLogger(__name__)

class LoginView(APIView):
    def post(self, request):
        username = request.data.get('username')
        password = request.data.get('password')
        user = authenticate(request, username=username, password=password)

        if user is not None:
            login(request, user)
            return JsonResponse({'message': 'Login successful'})
        return JsonResponse({'error': 'Invalid credentials'}, status=HTTPStatus.BAD_REQUEST)

class LogoutView(APIView):
    def post(self, request):
        logout(request)
        return JsonResponse({'message': 'Logout successful'})

class IsAuthenticatedView(APIView):
    def get(self, request):
        if request.user.is_authenticated:
            return JsonResponse({'authenticated': True})
        return JsonResponse({'authenticated': False}, status=HTTPStatus.UNAUTHORIZED)
