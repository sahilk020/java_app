<!--begin::Header-->
<div id="kt_header" class="header align-items-stretch">
  <!--begin::Container-->
  <div class="container-fluid d-flex align-items-stretch justify-content-between">
    <!--begin::Aside mobile toggle-->
    <div class="d-flex align-items-center d-lg-none ms-n3 me-1" title="Show aside menu">
      <div class="btn btn-icon btn-active-color-white" id="kt_aside_mobile_toggle">
        <i class="bi bi-list fs-1"></i>
      </div>
    </div>
    <!--end::Aside mobile toggle-->
    <!--begin::Mobile logo-->
    <div class="d-flex align-items-center flex-grow-1 flex-lg-grow-0">
      <a href="index.html" class="d-lg-none">
        <img alt="Logo" src="../assets/media/images/paylogo.svg" class="h-25px" />
      </a>
    </div>
    <!--end::Mobile logo-->
    <!--begin::Wrapper-->
    <div class="d-flex align-items-stretch justify-content-end flex-lg-grow-1">
      
      <!--begin::Toolbar wrapper-->
      <div class="topbar d-flex align-items-stretch flex-shrink-0">
        <!--begin::User-->
        <div class="d-flex align-items-stretch" id="kt_header_user_menu_toggle">
          <!--begin::Menu wrapper-->
          <div class="topbar-item cursor-pointer symbol px-3 px-lg-5 me-n3 me-lg-n5 symbol-30px symbol-md-35px" data-kt-menu-trigger="click" data-kt-menu-attach="parent" data-kt-menu-placement="bottom-end" data-kt-menu-flip="bottom">
            <img src="../assets/media/avatars/300-1.jpg" alt="metronic" style="border-radius: 15px;"/>
          </div>
          <!--begin::User account menu-->
          <div class="menu menu-sub menu-sub-dropdown menu-column menu-rounded menu-gray-800 menu-state-bg menu-state-color fw-semibold py-4 fs-6 w-275px" data-kt-menu="true">
            <!--begin::Menu item-->
            <div class="menu-item px-3">
              <div class="menu-content d-flex align-items-center px-3">
                <!--begin::Avatar-->
                <div class="symbol symbol-50px me-5">
                 <!--  <img alt="Logo" src="../assets/media/avatars/300-1.jpg" style="border-radius: 24px;"/> -->
                </div>
                <!--end::Avatar-->
                <!--begin::Username-->
                <div class="d-flex flex-column">
                  <div class="fw-bold d-flex align-items-center fs-5 mt-5"><s:property value="%{#session.USER.firstName}" /></div>
                  <p class="fw-semibold text-muted text-hover-primary fs-7"><s:property value="%{#session.USER.group.name}" /></p>
                </div>
                <!--end::Username-->
              </div>
            </div>
            <!--end::Menu item-->
            <!--begin::Menu separator-->
            <div class="separator my-2"></div>
            <!--end::Menu separator-->
            <!--begin::Menu item-->
            <s:iterator value="%{#session.ACCESSIBLE_MENUS}">
                <s:set var="dropdownForSubMenu" value="needToShowInProfile"></s:set>
                <s:if test="%{needToShowInProfile == true}">
                  <div class="menu-item px-5" data-kt-menu-trigger="{default: 'click', lg: 'hover'}" data-kt-menu-placement="left-start">
                      <a href="#" class="menu-link px-5">
                        <span class="menu-title"><s:property value = "menuName"/></span>
                        <span class="menu-arrow"></span>
                      </a>
                </s:if>
                <s:if test="%{#dropdownForSubMenu == true}">
                  <div class="menu-sub menu-sub-dropdown w-175px py-4">
                      <s:iterator value="subMenus">
                        <s:if test="%{needToShowInProfile == true}">
                          <div class="menu-item px-5">
                              <a href='<s:property value = "actionName"/>' class="menu-link px-5"><s:property value = "menuName"/></a>
                          </div>
                        </s:if>
                    </s:iterator>
                    </div>
                  </div>
                </s:if>
                <s:if test="%{#dropdownForSubMenu == false}">
                <s:iterator value="subMenus">
                    <s:if test="%{needToShowInProfile == true}">
                      <div class="menu-item px-5">
                          <a href='<s:property value = "actionName"/>' class="menu-link px-5"><s:property value = "menuName"/></a>
                      </div>
                    </s:if>
                </s:iterator>
                </s:if>
            </s:iterator>
            <!--end::Menu item-->
            <!--begin::Menu item-->
            <div class="menu-item px-5">
              <a href="logout" class="menu-link px-5">Sign Out</a>
            </div>
            <!--end::Menu item-->
          </div>
          <!--end::User account menu-->
          <!--end::Menu wrapper-->
        </div>
        <!--end::User -->
      </div>
      <!--end::Toolbar wrapper-->
    </div>
    <!--end::Wrapper-->
  </div>
  <!--end::Container-->
</div>
<!--end::Header-->