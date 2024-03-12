declare namespace API {
  type addUsingGETParams = {
    /** name */
    name?: string;
  };

  type AiResponse = {
    resultId?: number;
  };

  type BaseResponse = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type BaseResponseAiResponse_ = {
    code?: number;
    data?: AiResponse;
    message?: string;
  };

  type BaseResponseBiResponse_ = {
    code?: number;
    data?: BiResponse;
    message?: string;
  };

  type BaseResponseBoolean_ = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseChartGen_ = {
    code?: number;
    data?: ChartGen;
    message?: string;
  };

  type BaseResponseCredit_ = {
    code?: number;
    data?: Credit;
    message?: string;
  };

  type BaseResponseLoginUserVO_ = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type BaseResponseLong_ = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponsePage_ = {
    code?: number;
    data?: Page;
    message?: string;
  };

  type BaseResponsePageChartGen_ = {
    code?: number;
    data?: PageChartGen_;
    message?: string;
  };

  type BaseResponsePageCredit_ = {
    code?: number;
    data?: PageCredit_;
    message?: string;
  };

  type BaseResponsePagePointChanges_ = {
    code?: number;
    data?: PagePointChanges_;
    message?: string;
  };

  type BaseResponsePageTextTask_ = {
    code?: number;
    data?: PageTextTask_;
    message?: string;
  };

  type BaseResponsePageUser_ = {
    code?: number;
    data?: PageUser_;
    message?: string;
  };

  type BaseResponsePageUserVO_ = {
    code?: number;
    data?: PageUserVO_;
    message?: string;
  };

  type BaseResponseString_ = {
    code?: number;
    data?: string;
    message?: string;
  };

  type BaseResponseTextTask_ = {
    code?: number;
    data?: TextTask;
    message?: string;
  };

  type BaseResponseTextTaskVO_ = {
    code?: number;
    data?: TextTaskVO;
    message?: string;
  };

  type BaseResponseUser_ = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserVO_ = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type BiResponse = {
    chartId?: number;
    genChart?: string;
    genResult?: string;
  };

  type ChartAddRequest = {
    chartData?: string;
    chartName?: string;
    chartType?: string;
    goal?: string;
  };

  type ChartEditRequest = {
    chartData?: string;
    chartName?: string;
    chartType?: string;
    goal?: string;
    id?: number;
  };

  type ChartGen = {
    chartId?: number;
    chartType?: string;
    createTime?: string;
    execMessage?: string;
    genChart?: string;
    genResult?: string;
    goal?: string;
    id?: string;
    name?: string;
    status?: string;
    userId?: number;
    version?: number;
  };

  type ChartQueryRequest = {
    chartName?: string;
    chartType?: string;
    current?: number;
    goal?: string;
    id?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    userId?: number;
  };

  type ChartUpdateRequest = {
    chartData?: string;
    chartName?: string;
    chartType?: string;
    createTime?: string;
    genChart?: string;
    genResult?: string;
    goal?: string;
    id?: number;
    updateTime?: string;
    userId?: number;
  };

  type Credit = {
    checkTime?: string;
    createTime?: string;
    creditTotal?: number;
    id?: number;
    isDelete?: number;
    updateTime?: string;
    userId?: number;
  };

  type CreditAddRequest = {
    creditTotal?: number;
    userId?: number;
  };

  type CreditEditRequest = {
    creditTotal?: number;
    id?: number;
  };

  type CreditQueryRequest = {
    createTime?: string;
    creditTotal?: number;
    current?: number;
    id?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    updateTime?: string;
    userId?: number;
  };

  type CreditUpdateRequest = {
    creditTotal?: number;
    id?: number;
  };

  type DeleteChartDocRequest = {
    id?: number;
    version?: number;
  };

  type DeleteRequest = {
    id?: number;
  };

  type genChartByAiUsingPOSTParams = {
    chartName?: string;
    chartType?: string;
    goal?: string;
  };

  type genTextTaskAiUsingPOSTParams = {
    name?: string;
    textType?: string;
  };

  type genTextTaskAsyncAiMqUsingPOSTParams = {
    name?: string;
    textType?: string;
  };

  type genTextTaskAsyncAiRebuildUsingPOSTParams = {
    id?: number;
  };

  type getChartEntityByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getCreditByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getTextTaskByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getTextTaskVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type LoginUserVO = {
    createTime?: string;
    id?: number;
    updateTime?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type OrderItem = {
    asc?: boolean;
    column?: string;
  };

  type Page = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: Record<string, any>[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type Pageable = {
    offset?: number;
    pageNumber?: number;
    pageSize?: number;
    paged?: boolean;
    sort?: Sort;
    unpaged?: boolean;
  };

  type PageChartGen_ = {
    content?: ChartGen[];
    empty?: boolean;
    first?: boolean;
    last?: boolean;
    number?: number;
    numberOfElements?: number;
    pageable?: Pageable;
    size?: number;
    sort?: Sort;
    totalElements?: number;
    totalPages?: number;
  };

  type PageCredit_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: Credit[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PagePointChanges_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: PointChanges[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
  };

  type PageTextTask_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: TextTask[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUser_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: User[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type payUsingGETParams = {
    subject?: string;
    totalAmount?: number;
  };

  type PointChanges = {
    changeAmount?: number;
    changeType?: number;
    createTime?: string;
    id?: number;
    isDelete?: number;
    newPoints?: number;
    reason?: string;
    source?: string;
    userId?: number;
  };

  type regenerateChartUsingGETParams = {
    /** chartId */
    chartId: number;
  };

  type Sort = {
    empty?: boolean;
    sorted?: boolean;
    unsorted?: boolean;
  };

  type TextAddRequest = true;

  type TextEditRequest = {
    genTextContent?: string;
    id?: number;
    name?: string;
    status?: string;
    textType?: string;
    userId?: number;
  };

  type TextTask = {
    createTime?: string;
    execMessage?: string;
    genTextContent?: string;
    id?: number;
    isDelete?: number;
    name?: string;
    status?: string;
    textType?: string;
    updateTime?: string;
    userId?: number;
  };

  type TextTaskQueryRequest = {
    current?: number;
    genTextContent?: string;
    id?: number;
    name?: string;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    status?: string;
    textType?: string;
    userId?: number;
  };

  type TextTaskVO = {
    createTime?: string;
    genTextContent?: string;
    id?: number;
    name?: string;
    status?: string;
    textType?: string;
  };

  type TextUpdateRequest = {
    execMessage?: string;
    genTextContent?: string;
    id?: number;
    name?: string;
    status?: string;
    textType?: string;
  };

  type uploadFileUsingPOSTParams = {
    biz?: string;
  };

  type uploadImageAnalysisUsingPOSTParams = {
    biz?: string;
    goal?: string;
  };

  type User = {
    createTime?: string;
    id?: number;
    isDelete?: number;
    updateTime?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userRole?: string;
  };

  type UserAddRequest = {
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userRole?: string;
  };

  type UserLoginRequest = {
    userAccount?: string;
    userPassword?: string;
  };

  type UserQueryRequest = {
    current?: number;
    id?: number;
    mpOpenId?: string;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    unionId?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserRegisterRequest = {
    checkPassword?: string;
    userAccount?: string;
    userPassword?: string;
  };

  type UserUpdateMyRequest = {
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
  };

  type UserUpdateRequest = {
    id?: number;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserVO = {
    createTime?: string;
    id?: number;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };
}
