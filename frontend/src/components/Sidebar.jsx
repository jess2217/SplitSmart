import {
    LayoutDashboard,
    Users,
    Receipt,
    Wallet,
    ArrowRightLeft,
    Plus,
    X,
    LogOut
} from "lucide-react";

import { useState } from "react";

function Sidebar({
    activePage,
    setActivePage,
    open,
    setOpen,
    onAddExpense,
    onLogout,
    currentUser
}) {

    const [profileOpen, setProfileOpen] =
        useState(false);

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

    function handleAddExpense() {
        setOpen(false);

        if (onAddExpense) {
            onAddExpense();
        }
    }

    function handleLogout() {

        setProfileOpen(false);
        setOpen(false);

        if (onLogout) {
            onLogout();
        }
    }

    const userName =
        currentUser?.name || "User";

    const userInitial =
        userName
            .charAt(0)
            .toUpperCase();

    return (
        <>
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
                        className="mobile-close"
                        onClick={() => setOpen(false)}
                    >
                        <X size={20} />
                    </button>

                </div>

                <div className="sidebar-section-title">
                    MENU
                </div>

                <nav className="sidebar-nav">

                    {items.map((item) => {

                        const Icon = item.icon;

                        return (
                            <button
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

                <div className="sidebar-bottom">

                    <button
                        type="button"
                        className="quick-card"
                        onClick={handleAddExpense}
                    >

                        <div className="quick-icon">
                            <Plus size={17} />
                        </div>

                        <div>

                            <strong>
                                Add an expense
                            </strong>

                            <small>
                                Split your latest bill
                            </small>

                        </div>

                    </button>

                    {/* PROFILE */}

                    <div className="profile-wrapper">

                        <button
                            type="button"
                            className="profile-button"
                            onClick={() =>
                                setProfileOpen(
                                    !profileOpen
                                )
                            }
                        >

                            <div className="profile-avatar">
                                {userInitial}
                            </div>

                            <div className="profile-info">

                                <strong>
                                    {userName}
                                </strong>

                                <small>
                                    Student account
                                </small>

                            </div>

                        </button>

                        {profileOpen && (

                            <div className="profile-menu">

                                <button
                                    type="button"
                                    className="logout-button"
                                    onClick={handleLogout}
                                >

                                    <LogOut size={18} />

                                    <span>
                                        Logout
                                    </span>

                                </button>

                            </div>

                        )}

                    </div>

                </div>

            </aside>
        </>
    );
}

export default Sidebar;