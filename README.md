# retrofit-form

retrofit-form 支持使用 retrofit 发起 form data 请求时，使用自定义的对象作为参数。使用方式如下:

    public interface AccountService {
    
        @POST("/accounts")
        Call<AccountInfo> addAccountInfo(@Body @FormBody AccountInfo accountInfo);
    }
    
即将 form data 的参数定义到一个复杂对象里，在参数定义上添加 ``@FormBody`` 注解.
 
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8080/")
            .addConverterFactory(new FormBodyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    AccountService accountService = retrofit.create(AccountService.class);
    AccountInfo accountInfo = accountService.addAccountInfo(
            new AccountInfo()
                    .setEmail("bphanzhu@live.com")
                    .setBio("love 7777"))
            .execute()
            .body();

然后创建 Retrofit 对象时，添加 FormBodyConverterFactory.


