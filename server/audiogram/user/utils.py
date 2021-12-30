from PIL import Image, ImageOps
from django.conf import settings


def convert_image_to_png(request_user):
    img = Image.open(request_user.image.path)

    mask = Image.open(f'{settings.MEDIA_ROOT}/mask.png').convert('L')

    output = ImageOps.fit(img, mask.size, centering=(0.5, 0.5))
    output.putalpha(mask)

    request_user.image.name = request_user.image.name.split('.')[0] + '.png'

    new_path = request_user.image.path.split('.')[0] + '.png'

    output.save(new_path)
    request_user.save()
