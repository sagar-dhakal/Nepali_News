/*
  NepalNews
  <p/>
  Copyright (c) 2019-2020 Sagar Dhakal
  <p/>
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  <p/>
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  <p/>
  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.newstoday.nepalnews.items;


import java.util.List;

public class NepalNewsItem {

    public List<NepalNews> NepalNews;

    public static class NepalNews {
        public List<Categories> Categories;
        public List<Location> Location;
        public List<OnlineTools> OnlineTools;
        public List<SocialMedia> SocialMedia;
        public List<TopNewsSites> TopNewsSites;
        public List<RecentRssLinks> RecentRssLinks;
        public List<NepaliRadios> NepaliRadios;

        public static class Categories {
            public Entertainment Entertainment;
            public Finance Finance;
            public Health Health;
            public Politics Politics;
            public Sports Sports;
            public Technology Technology;
            public World World;

            public static class Entertainment {
                public List<Entertainmentnewssites> Entertainmentnewssites;
                public List<EntertainmentrssLinks> EntertainmentrssLinks;

                public static class Entertainmentnewssites {
                    public String siteImage;
                    public String siteLink;
                    public String siteName;
                }

                public static class EntertainmentrssLinks {
                    public String rssLink1;
                    public String rssLink10;
                    public String rssLink11;
                    public String rssLink12;
                    public String rssLink13;
                    public String rssLink14;
                    public String rssLink15;
                    public String rssLink2;
                    public String rssLink3;
                    public String rssLink4;
                    public String rssLink5;
                    public String rssLink6;
                    public String rssLink7;
                    public String rssLink8;
                    public String rssLink9;
                }
            }

            public static class Finance {
                public List<Financenewssites> Financenewssites;
                public List<FinancerssLinks> FinancerssLinks;

                public static class Financenewssites {
                    public String siteImage;
                    public String siteLink;
                    public String siteName;
                }

                public static class FinancerssLinks {
                    public String rssLink1;
                    public String rssLink10;
                    public String rssLink11;
                    public String rssLink12;
                    public String rssLink13;
                    public String rssLink14;
                    public String rssLink15;
                    public String rssLink2;
                    public String rssLink3;
                    public String rssLink4;
                    public String rssLink5;
                    public String rssLink6;
                    public String rssLink7;
                    public String rssLink8;
                    public String rssLink9;
                }
            }

            public static class Health {
                public List<Healthnewssites> Healthnewssites;
                public List<HealthrssLinks> HealthrssLinks;

                public static class Healthnewssites {
                    public String siteImage;
                    public String siteLink;
                    public String siteName;
                }

                public static class HealthrssLinks {
                    public String rssLink1;
                    public String rssLink10;
                    public String rssLink11;
                    public String rssLink12;
                    public String rssLink13;
                    public String rssLink14;
                    public String rssLink15;
                    public String rssLink2;
                    public String rssLink3;
                    public String rssLink4;
                    public String rssLink5;
                    public String rssLink6;
                    public String rssLink7;
                    public String rssLink8;
                    public String rssLink9;
                }
            }

            public static class Politics {
                public List<Politicsnewssites> Politicsnewssites;
                public List<PoliticsrssLinks> PoliticsrssLinks;

                public static class Politicsnewssites {
                    public String siteImage;
                    public String siteLink;
                    public String siteName;
                }

                public static class PoliticsrssLinks {
                    public String rssLink1;
                    public String rssLink10;
                    public String rssLink11;
                    public String rssLink12;
                    public String rssLink13;
                    public String rssLink14;
                    public String rssLink15;
                    public String rssLink2;
                    public String rssLink3;
                    public String rssLink4;
                    public String rssLink5;
                    public String rssLink6;
                    public String rssLink7;
                    public String rssLink8;
                    public String rssLink9;
                }
            }

            public static class Sports {
                public List<Sportsnewssites> Sportsnewssites;
                public List<SportsrssLinks> SportsrssLinks;

                public static class Sportsnewssites {
                    public String siteImage;
                    public String siteLink;
                    public String siteName;
                }

                public static class SportsrssLinks {
                    public String rssLink1;
                    public String rssLink10;
                    public String rssLink11;
                    public String rssLink12;
                    public String rssLink13;
                    public String rssLink14;
                    public String rssLink15;
                    public String rssLink2;
                    public String rssLink3;
                    public String rssLink4;
                    public String rssLink5;
                    public String rssLink6;
                    public String rssLink7;
                    public String rssLink8;
                    public String rssLink9;
                }
            }

            public static class Technology {
                public List<Technologynewssites> Technologynewssites;
                public List<TechnologyrssLinks> TechnologyrssLinks;

                public static class Technologynewssites {
                    public String siteImage;
                    public String siteLink;
                    public String siteName;
                }

                public static class TechnologyrssLinks {
                    public String rssLink1;
                    public String rssLink10;
                    public String rssLink11;
                    public String rssLink12;
                    public String rssLink13;
                    public String rssLink14;
                    public String rssLink15;
                    public String rssLink2;
                    public String rssLink3;
                    public String rssLink4;
                    public String rssLink5;
                    public String rssLink6;
                    public String rssLink7;
                    public String rssLink8;
                    public String rssLink9;
                }
            }

            public static class World {
                public List<Worldnewssites> Worldnewssites;
                public List<WorldrssLinks> WorldrssLinks;

                public static class Worldnewssites {
                    public String siteImage;
                    public String siteLink;
                    public String siteName;
                }

                public static class WorldrssLinks {
                    public String rssLink1;
                    public String rssLink10;
                    public String rssLink11;
                    public String rssLink12;
                    public String rssLink13;
                    public String rssLink14;
                    public String rssLink15;
                    public String rssLink2;
                    public String rssLink3;
                    public String rssLink4;
                    public String rssLink5;
                    public String rssLink6;
                    public String rssLink7;
                    public String rssLink8;
                    public String rssLink9;
                }
            }
        }

        public static class Location {
            public Province1 Province1;
            public Province2 Province2;
            public Province3 Province3;
            public Province4 Province4;
            public Province5 Province5;
            public Province6 Province6;
            public Province7 Province7;

            public static class Province1 {
                public List<Province1newssites> Province1newssites;
                public List<Province1rssLinks> Province1rssLinks;

                public static class Province1newssites {
                    public String siteImage;
                    public String siteLink;
                    public String siteName;
                }

                public static class Province1rssLinks {
                    public String rssLink1;
                    public String rssLink10;
                    public String rssLink11;
                    public String rssLink12;
                    public String rssLink13;
                    public String rssLink14;
                    public String rssLink15;
                    public String rssLink2;
                    public String rssLink3;
                    public String rssLink4;
                    public String rssLink5;
                    public String rssLink6;
                    public String rssLink7;
                    public String rssLink8;
                    public String rssLink9;
                }
            }

            public static class Province2 {
                public List<Province2newssites> Province2newssites;
                public List<Province2rssLinks> Province2rssLinks;

                public static class Province2newssites {
                    public String siteImage;
                    public String siteLink;
                    public String siteName;
                }

                public static class Province2rssLinks {
                    public String rssLink1;
                    public String rssLink10;
                    public String rssLink11;
                    public String rssLink12;
                    public String rssLink13;
                    public String rssLink14;
                    public String rssLink15;
                    public String rssLink2;
                    public String rssLink3;
                    public String rssLink4;
                    public String rssLink5;
                    public String rssLink6;
                    public String rssLink7;
                    public String rssLink8;
                    public String rssLink9;
                }
            }

            public static class Province3 {
                public List<Province3newssites> Province3newssites;
                public List<Province3rssLinks> Province3rssLinks;

                public static class Province3newssites {
                    public String siteImage;
                    public String siteLink;
                    public String siteName;
                }

                public static class Province3rssLinks {
                    public String rssLink1;
                    public String rssLink10;
                    public String rssLink11;
                    public String rssLink12;
                    public String rssLink13;
                    public String rssLink14;
                    public String rssLink15;
                    public String rssLink2;
                    public String rssLink3;
                    public String rssLink4;
                    public String rssLink5;
                    public String rssLink6;
                    public String rssLink7;
                    public String rssLink8;
                    public String rssLink9;
                }
            }

            public static class Province4 {
                public List<Province4newssites> Province4newssites;
                public List<Province4rssLinks> Province4rssLinks;

                public static class Province4newssites {
                    public String siteImage;
                    public String siteLink;
                    public String siteName;
                }

                public static class Province4rssLinks {
                    public String rssLink1;
                    public String rssLink10;
                    public String rssLink11;
                    public String rssLink12;
                    public String rssLink13;
                    public String rssLink14;
                    public String rssLink15;
                    public String rssLink2;
                    public String rssLink3;
                    public String rssLink4;
                    public String rssLink5;
                    public String rssLink6;
                    public String rssLink7;
                    public String rssLink8;
                    public String rssLink9;
                }
            }

            public static class Province5 {
                public List<Province5newssites> Province5newssites;
                public List<Province5rssLinks> Province5rssLinks;

                public static class Province5newssites {
                    public String siteImage;
                    public String siteLink;
                    public String siteName;
                }

                public static class Province5rssLinks {
                    public String rssLink1;
                    public String rssLink10;
                    public String rssLink11;
                    public String rssLink12;
                    public String rssLink13;
                    public String rssLink14;
                    public String rssLink15;
                    public String rssLink2;
                    public String rssLink3;
                    public String rssLink4;
                    public String rssLink5;
                    public String rssLink6;
                    public String rssLink7;
                    public String rssLink8;
                    public String rssLink9;
                }
            }

            public static class Province6 {
                public List<Province6newssites> Province6newssites;
                public List<Province6rssLinks> Province6rssLinks;

                public static class Province6newssites {
                    public String siteImage;
                    public String siteLink;
                    public String siteName;
                }

                public static class Province6rssLinks {
                    public String rssLink1;
                    public String rssLink10;
                    public String rssLink11;
                    public String rssLink12;
                    public String rssLink13;
                    public String rssLink14;
                    public String rssLink15;
                    public String rssLink2;
                    public String rssLink3;
                    public String rssLink4;
                    public String rssLink5;
                    public String rssLink6;
                    public String rssLink7;
                    public String rssLink8;
                    public String rssLink9;
                }
            }

            public static class Province7 {
                public List<Province7newssites> Province7newssites;
                public List<Province7rssLinks> Province7rssLinks;

                public static class Province7newssites {
                    public String siteImage;
                    public String siteLink;
                    public String siteName;
                }

                public static class Province7rssLinks {
                    public String rssLink1;
                    public String rssLink10;
                    public String rssLink11;
                    public String rssLink12;
                    public String rssLink13;
                    public String rssLink14;
                    public String rssLink15;
                    public String rssLink2;
                    public String rssLink3;
                    public String rssLink4;
                    public String rssLink5;
                    public String rssLink6;
                    public String rssLink7;
                    public String rssLink8;
                    public String rssLink9;
                }
            }
        }

        public static class OnlineTools {
            public List<OnlineShopping> OnlineShopping;
            public List<HotelBooking> HotelBooking;
            public List<JobSites> JobSites;
            public List<EducationSites> EducationSites;
            public List<OtherSites> OtherSites;

            public static class OnlineShopping {
                public String siteImage;
                public String siteLink;
                public String siteName;
            }

            public static class HotelBooking {
                public String siteImage;
                public String siteLink;
                public String siteName;
            }

            public static class JobSites {
                public String siteImage;
                public String siteLink;
                public String siteName;
            }

            public static class EducationSites {
                public String siteImage;
                public String siteLink;
                public String siteName;
            }

            public static class OtherSites {
                public String siteImage;
                public String siteLink;
                public String siteName;
            }
        }

        public static class SocialMedia {
            public String siteImage;
            public String siteLink;
            public String siteName;
        }

        public static class TopNewsSites {
            public String siteImage;
            public String siteLink;
            public String siteName;
        }

        public static class RecentRssLinks {
            public String rssLink1;
            public String rssLink10;
            public String rssLink11;
            public String rssLink12;
            public String rssLink13;
            public String rssLink14;
            public String rssLink15;
            public String rssLink2;
            public String rssLink3;
            public String rssLink4;
            public String rssLink5;
            public String rssLink6;
            public String rssLink7;
            public String rssLink8;
            public String rssLink9;
        }

        public static class NepaliRadios {
            public String stationDetail;
            public String stationLink;
            public String stationLocation;
            public String stationName;
            public String stationimage;
        }
    }
}
