from django.conf import settings
from django.contrib.sessions.middleware import SessionMiddleware
from django.utils.deprecation import MiddlewareMixin

class AuthorizationHeaderSessionMiddleware(SessionMiddleware):
    def process_request(self, request):
        auth_header = request.META.get('HTTP_AUTHORIZATION')
        if auth_header:
            session_key = auth_header.split(' ')[1] 
            request.COOKIES[settings.SESSION_COOKIE_NAME] = session_key

        super().process_request(request)


class DisableCSRFCheckMiddleware(MiddlewareMixin):
    def process_request(self, request):
        setattr(request, '_dont_enforce_csrf_checks', True)
