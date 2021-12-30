from django.http import JsonResponse

from user.models import User
from voice.models import Voice


def upload_voice(request):
    if request.method == "POST":
        try:
            token = request.POST.get("token")[1:-1]
            request_user = User.objects.get(token=token)
        except Exception:
            return JsonResponse({"success": False, "message": "User is not authenticated"})

        voice_file = request.FILES.get("file")
        if voice_file is None:
            return JsonResponse({"success": False, "message": "File is not uploaded"})

        Voice(owner=request_user, voice=voice_file).save()

        return JsonResponse({"success": True, "message": "Voice is uploaded"})
