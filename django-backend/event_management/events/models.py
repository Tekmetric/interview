# events/models.py
from django.db import models
from django.contrib.auth.models import User

class Event(models.Model):
    title = models.CharField(max_length=200)
    description = models.TextField()
    event_datetime = models.DateTimeField()
    event_image_url = models.URLField(max_length=200, blank=True, null=True)
    created_by = models.ForeignKey(User, on_delete=models.CASCADE)

    def __str__(self):
        return self.title
