from django.urls import path
from .views import (
    EventListCreateView,
    EventRetrieveUpdateDestroyView,
    LoginView,
    LogoutView,
    IsAuthenticatedView,
)

urlpatterns = [
    path('events/', EventListCreateView.as_view(), name='event-list-create'),
    path('events/<int:pk>/', EventRetrieveUpdateDestroyView.as_view(), name='event-detail'),
    path('login/', LoginView.as_view(), name='login'),
    path('logout/', LogoutView.as_view(), name='logout'),
    path('is-authenticated/', IsAuthenticatedView.as_view(), name='is-authenticated'),
]
