package com.tradesk.network

import com.tradesk.Model.*
import com.tradesk.data.entity.MoveImagesInFolderModel
import com.tradesk.data.entity.MoveImagesJobToProfileModel
import com.tradesk.util.NetworkConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface EasyBuliderAPI {
    @FormUrlEncoded
    @POST(NetworkConstants.login)
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_token") device_token: String,
        @Field("device_type") device_type: String
    ): Response<LoginModel>

    @FormUrlEncoded
    @POST(NetworkConstants.forgot)
    suspend fun forgot(
        @Field("email") email: String
    ): Response<SuccessModel>

    @FormUrlEncoded
    @POST(NetworkConstants.signup)
    suspend fun signup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("address") address: String,
        @Field("password") password: String,
        @Field("userType") userType: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("postal_code") postal_code: String,
        @Field("trade") trade: String,
        @Field("latLong") latLong: String
    ): Response<SignupModel>

    @FormUrlEncoded
    @POST(NetworkConstants.socialLogin)
    suspend fun socialLogin(
        @Field("address") address: String,
        @Field("accessToken") password: String,
        @Field("device_token") device_token: String,
        @Field("device_type") device_type: String,
        @Field("userType") userType: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("postal_code") postal_code: String,
        @Field("trade") trade: String,
        @Field("latLong") latLong: String
    ): Response<LoginModel>

    @GET(NetworkConstants.getProfile)
    suspend fun getProfile(): Response<ProfileModel>

    @Multipart
    @POST(NetworkConstants.editprofile)
    suspend fun editProfile(@PartMap map: HashMap<String, RequestBody>): Response<SuccessModel>

    @Multipart
    @POST(NetworkConstants.editprofiledocs)
    suspend fun editprofiledocs(@Part addd:ArrayList<MultipartBody.Part>): Response<SuccessModel>

    @GET(NetworkConstants.getleads)
    suspend fun getleads(
        @Query("type") type: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("status") status: String
    ): Response<LeadsModel>

    @GET(NetworkConstants.leaddelete + "{id}")
    suspend fun deleteLead(@Path("id") id: String): Response<SuccessModel>

    @GET(NetworkConstants.leaddetail + "{id}")
    suspend fun getLeadDetail(@Path("id") id: String): Response<LeadDetailModel>

    @FormUrlEncoded
    @POST(NetworkConstants.convertleads)
    suspend fun convertleads(
        @Field("_id") id: String,
        @Field("type") type: String,
        @Field("status") status: String,
        @Field("converted_to_job") converted_to_job: String
    ): Response<SuccessModel>

    @FormUrlEncoded
    @POST(NetworkConstants.addreminder)
    suspend fun addreminder(
        @Field("id") id: String,
        @Field("client_id") client_id: String,
        @Field("remainder_type") remainder_type: String,
        @Field("type") type: String,
        @Field("dateTime") dateTime: String,
        @Field("timezone") timezone: String
    ): Response<SuccessModel>

    @Multipart
    @POST(NetworkConstants.addleads)
    suspend fun addleads(
        @PartMap map: HashMap<String, RequestBody>,
        @Part("users_assigned") users: ArrayList<RequestBody?>?
    ): Response<SuccessModel>

    @Multipart
    @PUT(NetworkConstants.updateLeads)
    suspend fun updateLeads(
        @PartMap map: HashMap<String, RequestBody>,
        @Part("users_assigned") users: ArrayList<RequestBody?>?
    ): Response<SuccessModel>

    @GET(NetworkConstants.clientdetails + "{id}")
    suspend fun clientdetails(
        @Path("id") id: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("status") status: String,
        @Query("type") type: String
    ): Response<ClientSalesModelNew>


    @GET(NetworkConstants.clientslist)
    suspend fun clientslist(
        @Query("type") type: String,
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Response<ClientsListModel>

    @GET(NetworkConstants.clientslist)
    suspend fun saleslist(
        @Query("type") type: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("trade") trade: String
    ): Response<ClientsListModel>

    @Multipart
    @POST(NetworkConstants.addclient)
    suspend fun addclient(@PartMap map: HashMap<String, RequestBody>):Response<SuccessModel>

    @Multipart
    @PUT(NetworkConstants.updatesaleclient)
    suspend fun updatesaleclient(@PartMap map: HashMap<String, RequestBody>): Response<SuccessModel>

    @HTTP(method = "DELETE", path = NetworkConstants.deleteSelectedClient, hasBody = true)
    suspend fun deleteSelectedClient(@Body selectedIds: SelectedIds): Response<SuccessModel>

    @FormUrlEncoded
    @POST(NetworkConstants.addjobsubusers)
    suspend fun addjobsubusers(
        @Field("client_id") client_id: String,
        @Field("job_id") job_id: String
    ): Response<SuccessModel>

    @GET(NetworkConstants.getadditionalimagesjobs)
    suspend fun getadditionalimagesjobs(
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("status") status: String
    ): Response<AdditionalImagesWithClientModel>

    @Multipart
    @POST(NetworkConstants.usersAddAlbum)
    suspend fun usersAddAlbum(@PartMap map: HashMap<String, RequestBody>,@Part addd:ArrayList<MultipartBody.Part>): Response<SuccessModel>

    @Multipart
    @POST(NetworkConstants.add_addtional_images)
    suspend fun add_multiple_addtional_images(@PartMap params:HashMap<String,RequestBody>,@Part addd:ArrayList<MultipartBody.Part>): Response<SuccessModel>

    @POST(NetworkConstants.usersMoveImagesJobToProfile)
    suspend fun usersMoveImagesJobToProfile(@Body moveImagesJobToProfileModel: MoveImagesJobToProfileModel)
            : Response<SuccessModel>

    @POST(NetworkConstants.moveAdditionalImages)
    suspend fun moveAdditionalImages(@Body moveAdditionalImagesModel: MoveAdditionalImagesModel)
            : Response<SuccessModel>

    @HTTP(method = "DELETE", path = NetworkConstants.deleteSelectedGallery, hasBody = true)
    suspend fun deleteSelectedGallery(@Body selectedImageIds:SelectedImageIds): Response<SuccessModel>

    @POST(NetworkConstants.moveImagesInFolder)
    suspend fun moveImagesInFolder(@Body moveImagesInFolderModel: MoveImagesInFolderModel)
            : Response<SuccessModel>

    @Multipart
    @POST(NetworkConstants.usersAddAdditionalImages)
    suspend fun usersAddAdditionalImages(@PartMap map: HashMap<String, RequestBody>,@Part addd:ArrayList<MultipartBody.Part>): Response<SuccessModel>

    @HTTP(method = "DELETE", path = NetworkConstants.userDelSelectedImages, hasBody = true)
    suspend fun userDelSelectedImages(@Body selectedUrlsUser:SelectedUrlsUser): Response<SuccessModel>

    @POST(NetworkConstants.userMoveImagesInFolder)
    suspend fun userMoveImagesUserToJob(@Body moveImagesUserToJob: MoveImagesUserToJob)
            : Response<SuccessModel>

    @POST(NetworkConstants.userMoveImagesInFolder)
    suspend fun userMoveImagesUserToUser(@Body moveImagesUserToUser: MoveImagesUserToUser)
            : Response<SuccessModel>

    @GET(NetworkConstants.calendardetail)
    suspend fun calendardetail(
        @Query("timezone") timezone: String,
        @Query("date") date: String,
        @Query("dateType") dateType: String
    ): Response<CalendarDetailModel>

    @Headers("Content-Type: application/json")
    @GET(NetworkConstants.deletereminder + "{id}")
    suspend fun deletereminderCal(@Path("id") id: String): Response<SuccessModel>

    @GET(NetworkConstants.getreminders)
    suspend fun getremindersdate(
        @Query("type") type: String,
        @Query("date") date: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("timezone") timezone: String,
    ): Response<RemindersModel>

    @FormUrlEncoded
    @POST(NetworkConstants.addreminder)
    suspend fun addreminderCal(
        @Field("remainder_type") remainder_type: String,
        @Field("type") type: String,
        @Field("dateTime") dateTime: String,
        @Field("description") datedescriptionTime: String,
        @Field("timezone") timezone: String
    ): Response<SuccessModel>

    @FormUrlEncoded
    @POST(NetworkConstants.editreminder + "{id}")
    suspend fun editreminderCal(
        @Path("id") id: String,
        @Field("remainder_type") remainder_type: String,
        @Field("type") type: String,
        @Field("dateTime") dateTime: String,
        @Field("description") datedescriptionTime: String,
        @Field("timezone") timezone: String
    ): Response<SuccessModel>

    @GET(NetworkConstants.getAllDocs)
    suspend fun getAllDocs(): Response<AllDocumentsModel>

    @Multipart
    @POST(NetworkConstants.usersAddAdditionalDocs)
    suspend fun usersAddAdditionalDocs(@PartMap map: HashMap<String, RequestBody>,@Part addd:ArrayList<MultipartBody.Part>): Response<SuccessModel>

    @DELETE(NetworkConstants.deleteAllDocumentsJobs)
    suspend fun deleteAllDocumentsJobs(@Query("job_id") job_id: String): Response<SuccessModel>

    @HTTP(method = "DELETE", path = NetworkConstants.deleteSelectedDocumentsJobs, hasBody = true)
    suspend fun deleteSelectedDocumentsJobs(@Body selectedDocsIds: SelectedDocsIds): Response<SuccessModel>

    @HTTP(method = "DELETE", path = NetworkConstants.usersDelSelectedAdditionalDocs, hasBody = true)
    suspend fun usersDelSelectedAdditionalDocs(@Body selectedUserDocsDel: SelectedUserDocsDel): Response<SuccessModel>

    @Multipart
    @POST(NetworkConstants.usersUpdateAdditionalDocs)
    suspend fun usersUpdateAdditionalDocs(@PartMap map: HashMap<String, RequestBody>,@Part addd:ArrayList<MultipartBody.Part>): Response<SuccessModel>

    @GET(NetworkConstants.proposallist)
    suspend fun getproposals(
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("status") status: String,
        @Query("type") type: String,
        @Query("contract_id") contract_id: String
    ): Response<PorposalsListModel>

    @FormUrlEncoded
    @POST(NetworkConstants.sendproposal)
    suspend fun sendproposal(
        @Field("id") id: String,
        @Field("email") email: String
    ): Response<SuccessModel>

    @HTTP(method = "DELETE", path = NetworkConstants.deleteSelectedProposal, hasBody = true)
    suspend fun deleteSelectedProposals(@Body selectedIds: SelectedIds): Response<SuccessModel>

    @GET(NetworkConstants.proposaldetail + "{id}")
    suspend fun getProposalDetail(@Path("id") id: String): Response<ProposalDetailModel>

    @DELETE(NetworkConstants.deleteAllProposal)
    suspend fun deleteAllProposals(
        @Query("type") type: String,
        @Query("status") status: String
    ): Response<SuccessModel>

    @DELETE(NetworkConstants.deleteproposal + "{id}")
    suspend fun deleteproposal(@Path("id") id: String): Response<SuccessModel>

    @Multipart
    @POST(NetworkConstants.proposaladd)
    suspend fun addproposal(@PartMap map: HashMap<String, RequestBody>): Response<AddProposalsModel>

    @Multipart
    @POST(NetworkConstants.proposaladd)
    suspend fun addproposal(@PartMap map: HashMap<String, RequestBody>,
                    @Part("doc_url[]") docs: ArrayList<RequestBody>?,
                    @Part imagesList: ArrayList<MultipartBody.Part>
    ): Response<AddProposalsModel>

    @Multipart
    @POST(NetworkConstants.updateProposal)
    suspend fun updateProposal(@PartMap map: HashMap<String, RequestBody>):Response<AddProposalsModel>

    @Multipart
    @POST(NetworkConstants.updateProposal)
    suspend fun updateProposal(@PartMap map: HashMap<String,
            RequestBody>,@Part("doc_url[]") docs: ArrayList<RequestBody>?,
                       @Part("existingDocs[]") existingDocs: ArrayList<RequestBody>?,
                       @Part imagesList: ArrayList<MultipartBody.Part>
    ): Response<AddProposalsModel>

    @GET(NetworkConstants.proposalItemslist)
    suspend fun proposalItemslist(): Response<DefaultItemsModel>

}