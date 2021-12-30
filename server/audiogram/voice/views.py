from django.http import JsonResponse
from django.contrib.humanize.templatetags.humanize import naturaltime
from user.models import User
from voice.models import Voice, Like, Comment


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


def get_profile_voices(request):
    if request.method == "POST":
        try:
            token = request.POST.get("token")
            request_user = User.objects.get(token=token)
        except Exception:
            return JsonResponse({"success": False, "message": "User is not authenticated"})

        username = request.POST.get('username')
        if username is None:
            user = request_user
        else:
            try:
                user = User.objects.get(username=username)
            except Exception:
                return JsonResponse({"success": False, "message": "User does not exist"})

        voices = Voice.objects.filter(owner=user)
        voices_result = []
        for voice in voices:
            voices_result.append({
                "username": user.username,
                "imageUrl": user.image.url,
                "voiceUrl": voice.voice.url,
                "publishDate": naturaltime(voice.publish_date),
                "likeNumbers": Like.objects.filter(voice=voice).count(),
                "commentNumbers": Comment.objects.filter(voice=voice).count()
            })

        return JsonResponse({"success": True, "voices": voices_result})
