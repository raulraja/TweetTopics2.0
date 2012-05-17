package api.loaders;


import android.content.Context;
import api.AsynchronousLoader;
import api.request.LoadLinkRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.LoadLinkResponse;
import com.javielinux.tweettopics2.Utils;
import infos.InfoLink;
import infos.InfoWeb;

public class LoadLinkLoader extends AsynchronousLoader<BaseResponse> {

    private LoadLinkRequest request;

    public LoadLinkLoader(Context context, LoadLinkRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        LoadLinkResponse response = new LoadLinkResponse();

        InfoLink il = request.getInfoLink();

        if (il.getType()==2) { // es un link
            try {
                InfoWeb info = new InfoWeb(il.getLink());

                if (!info.getTitle().equals("")) {
                    il.setTitle(info.getTitle());
                }
                if (!info.getDescription().equals("")) {
                    il.setDescription(info.getDescription());
                }
                if (info.getImageBitmap()!=null) {
                    il.setBitmapThumb(info.getImageBitmap());
                }

                il.setExtensiveInfo(true);
            } catch (Exception e) {
                e.printStackTrace();
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setError(e, e.getMessage());
                return errorResponse;
            }

        } else {
            int h = Utils.dip2px(getContext(), Utils.HEIGHT_IMAGE);
            if (il.getType()==1) h = Utils.dip2px(getContext(), Utils.HEIGHT_VIDEO);
            il.setBitmapLarge(Utils.getBitmap(il.getLinkImageLarge(), h));
            il.setExtensiveInfo(true);
        }

        response.setInfoLink(il);

        return response;


    }

}
