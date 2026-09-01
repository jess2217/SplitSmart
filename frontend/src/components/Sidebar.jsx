import {
    LayoutDashboard,
    Users,
    Receipt,
    Wallet,
    ArrowRightLeft,
    X,
    ChevronRight
} from "lucide-react";

function Sidebar({
    activePage,
    setActivePage,
    open,
    setOpen,
    currentUser
}) {

    const items = [
        {
            id: "dashboard",
            label: "Dashboard",
            icon: LayoutDashboard
        },
        {
            id: "groups",
            label: "Groups",
            icon: Users
        },
        {
            id: "members",
            label: "Members",
            icon: Users
        },
        {
            id: "expenses",
            label: "Expenses",
            icon: Receipt
        },
        {
            id: "balances",
            label: "Balances",
            icon: Wallet
        },
        {
            id: "settlements",
            label: "Settlements",
            icon: ArrowRightLeft
        }
    ];

    function navigate(id) {

        setActivePage(id);
        setOpen(false);
    }

    function openProfile() {

        setActivePage("profile");
        setOpen(false);
    }

    const userName =
        currentUser?.name || "User";

    const userEmail =
        currentUser?.email || "No email available";

    const userInitial =
        userName
            .charAt(0)
            .toUpperCase();

    return (
        <>
            {/* MOBILE OVERLAY */}

            {open && (
                <div
                    className="mobile-overlay"
                    onClick={() => setOpen(false)}
                />
            )}


            <aside
                className={`sidebar ${
                    open ? "sidebar-open" : ""
                }`}
            >

                {/* =========================
                    SIDEBAR HEADER
                ========================= */}

                <div className="sidebar-top">

                    <div className="brand">

                        <div className="brand-mark">
                            S
                        </div>

                        <div>

                            <div className="brand-name">
                                SplitSmart
                            </div>

                            <div className="brand-subtitle">
                                Expense manager
                            </div>

                        </div>

                    </div>


                    <button
                        type="button"
                        className="mobile-close"
                        onClick={() => setOpen(false)}
                    >

                        <X size={20} />

                    </button>

                </div>


                {/* =========================
                    MENU
                ========================= */}

                <div className="sidebar-section-title">
                    MENU
                </div>


                <nav className="sidebar-nav">

                    {items.map((item) => {

                        const Icon = item.icon;

                        return (
                            <button
                                type="button"
                                key={item.id}
                                className={`nav-item ${
                                    activePage === item.id
                                        ? "active"
                                        : ""
                                }`}
                                onClick={() =>
                                    navigate(item.id)
                                }
                            >

                                <Icon size={19} />

                                <span>
                                    {item.label}
                                </span>

                            </button>
                        );

                    })}

                </nav>


                {/* =========================
                    PROFILE
                ========================= */}

                <div className="sidebar-bottom">

                    <button
                        type="button"
                        className={`profile-button ${
                            activePage === "profile"
                                ? "profile-active"
                                : ""
                        }`}
                        onClick={openProfile}
                    >

                        <div className="profile-avatar">
                            {userInitial}
                        </div>


                        <div className="profile-info">

                            <strong>
                                {userName}
                            </strong>

                            <small>
                                {userEmail}
                            </small>

                        </div>


                        <ChevronRight
                            size={17}
                            className="profile-arrow"
                        />

                    </button>

                </div>

            </aside>
        </>
    );
}

export default Sidebar;